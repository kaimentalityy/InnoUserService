package com.innowise.userservice.service;

import com.innowise.userservice.exception.EntityAlreadyExistsException;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.AuthDto;
import com.innowise.userservice.model.dto.AuthResponseDto;
import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.dao.UserRepository;
import com.innowise.userservice.service.impl.CardInfoService;
import com.innowise.userservice.service.impl.UserService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceExtendedTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private CardInfoService cardInfoService;
    @Mock
    private Counter usersCreatedCounter;
    @Mock
    private Counter usersUpdatedCounter;
    @Mock
    private Counter usersDeletedCounter;
    @Mock
    private Timer userOperationTimer;
    @Mock
    private Keycloak keycloak;

    @Mock
    private RealmResource realmResource;
    @Mock
    private UsersResource usersResource;
    @Mock
    private RolesResource rolesResource;
    @Mock
    private RoleResource roleResource;
    @Mock
    private UserResource userResource;
    @Mock
    private RoleMappingResource roleMappingResource;
    @Mock
    private org.keycloak.admin.client.resource.RoleScopeResource roleScopeResource;
    @Mock
    private Response keycloakResponse;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "realm", "innowise-realm");
        ReflectionTestUtils.setField(userService, "keycloakServerUrl", "http://keycloak:8080");
        ReflectionTestUtils.setField(userService, "clientId", "innowise-client");
        ReflectionTestUtils.setField(userService, "clientSecret", "secret");

        when(userOperationTimer.record(any(Supplier.class))).thenAnswer(inv -> {
            Supplier<?> s = inv.getArgument(0);
            return s.get();
        });
        doAnswer(inv -> {
            Runnable r = inv.getArgument(0);
            r.run();
            return null;
        }).when(userOperationTimer).record(any(Runnable.class));
    }

    // ---- register ----

    @Test
    void register_emailAlreadyExistsInDb_throwsEntityAlreadyExists() {
        AuthDto dto = new AuthDto("john@example.com", "Password1!", "John", "Doe",
                LocalDate.of(1990, 1, 1));
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> userService.register(dto));
        verifyNoInteractions(keycloak);
    }

    @Test
    void register_emailAlreadyExistsInKeycloak_throwsEntityAlreadyExists() {
        AuthDto dto = new AuthDto("john@example.com", "Password1!", "John", "Doe",
                LocalDate.of(1990, 1, 1));
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(keycloak.realm("innowise-realm")).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        UserRepresentation existing = new UserRepresentation();
        existing.setEmail("john@example.com");
        when(usersResource.searchByEmail("john@example.com", true)).thenReturn(List.of(existing));

        assertThrows(EntityAlreadyExistsException.class, () -> userService.register(dto));
    }

    @Test
    void register_keycloakReturnsNon201_throwsRuntimeException() {
        AuthDto dto = new AuthDto("new@example.com", "Password1!", "Jane", "Doe",
                LocalDate.of(1995, 5, 15));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(keycloak.realm("innowise-realm")).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.searchByEmail("new@example.com", true)).thenReturn(Collections.emptyList());
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(keycloakResponse);
        when(keycloakResponse.getStatus()).thenReturn(500);
        when(keycloakResponse.getStatusInfo()).thenReturn(Response.Status.INTERNAL_SERVER_ERROR);

        assertThrows(RuntimeException.class, () -> userService.register(dto));
    }

    @Test
    void register_success_createsUserAndReturnsAuthResponse() {
        String keycloakId = "kc-uuid-123";
        AuthDto dto = new AuthDto("success@example.com", "Password1!", "Alice", "Smith",
                LocalDate.of(1992, 3, 10));

        when(userRepository.existsByEmail("success@example.com")).thenReturn(false);
        when(keycloak.realm("innowise-realm")).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.searchByEmail("success@example.com", true)).thenReturn(Collections.emptyList());
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(keycloakResponse);
        when(keycloakResponse.getStatus()).thenReturn(201);
        when(keycloakResponse.getHeaderString("Location"))
                .thenReturn("http://keycloak/admin/realms/innowise-realm/users/" + keycloakId);

        when(usersResource.get(keycloakId)).thenReturn(userResource);
        doNothing().when(userResource).resetPassword(any());

        when(realmResource.roles()).thenReturn(rolesResource);
        when(rolesResource.get("user")).thenReturn(roleResource);
        RoleRepresentation roleRep = new RoleRepresentation();
        roleRep.setName("user");
        when(roleResource.toRepresentation()).thenReturn(roleRep);

        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);
        doNothing().when(roleScopeResource).add(anyList());

        User savedUser = new User();
        savedUser.setId(keycloakId);
        savedUser.setEmail("success@example.com");
        savedUser.setName("Alice");
        savedUser.setSurname("Smith");
        savedUser.setBirthDate(LocalDate.of(1992, 3, 10));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toUserDto(savedUser)).thenReturn(
                new UserDto(keycloakId, "Alice", "Smith", LocalDate.of(1992, 3, 10), "success@example.com", null));

        AuthResponseDto result = userService.register(dto);

        assertNotNull(result);
        assertEquals(keycloakId, result.getId());
        assertEquals("success@example.com", result.getEmail());
        assertNull(result.getAccessToken());
    }

    // ---- update ----

    @Test
    void update_userExists_updatesAndReturnsDto() {
        String id = "a22be142-c4d7-47b1-bef3-f098381b8597";
        User existingUser = new User();
        existingUser.setId(id);

        UserDto updateDto = new UserDto(id, "Jane", "Doe", LocalDate.of(1990, 5, 20), "jane@example.com", null);
        UserDto updatedDto = new UserDto(id, "Jane", "Doe", LocalDate.of(1990, 5, 20), "jane@example.com", null);

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        doNothing().when(userMapper).updateUserFromDto(updateDto, existingUser);
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toUserDto(existingUser)).thenReturn(updatedDto);

        UserDto result = userService.update(id, updateDto);

        assertNotNull(result);
        assertEquals("Jane", result.name());
        verify(usersUpdatedCounter).increment();
    }

    @Test
    void update_userNotFound_throwsEntityNotFoundException() {
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.update("nonexistent",
                new UserDto(null, "X", "Y", LocalDate.now().minusYears(20), "x@y.com", null)));
    }

    // ---- findById ----

    @Test
    void findById_userExists_returnsDto() {
        String id = "a22be142-c4d7-47b1-bef3-f098381b8597";
        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(
                new UserDto(id, "John", "Doe", LocalDate.of(1990, 1, 1), "john@example.com", null));

        UserDto result = userService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
    }

    @Test
    void findById_notFound_throwsEntityNotFoundException() {
        when(userRepository.findById("nope")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findById("nope"));
    }

    // ---- deleteByEmail ----

    @Test
    void deleteByEmail_callsRepositoryDelete() {
        doNothing().when(userRepository).deleteByEmail("john@example.com");

        userService.deleteByEmail("john@example.com");

        verify(userRepository).deleteByEmail("john@example.com");
    }

    // ---- searchUsers ----

    @Test
    void searchUsers_withName_returnsPage() {
        User user = new User();
        user.setId("uuid-1");
        Page<User> page = new PageImpl<>(List.of(user));
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(userMapper.toUserDto(user)).thenReturn(
                new UserDto("uuid-1", "John", "Doe", LocalDate.of(1990, 1, 1), "john@example.com", null));

        Page<UserDto> result = userService.searchUsers("John", null, null, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void searchUsers_withAllFilters_returnsPage() {
        User user = new User();
        user.setId("uuid-2");
        Page<User> page = new PageImpl<>(List.of(user));
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(userMapper.toUserDto(user)).thenReturn(
                new UserDto("uuid-2", "Jane", "Smith", LocalDate.of(1992, 3, 10), "jane@example.com", null));

        Page<UserDto> result = userService.searchUsers("Jane", "Smith", "jane@example.com", pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void searchUsers_noFilters_returnsAllUsers() {
        Page<User> page = new PageImpl<>(Collections.emptyList());
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<UserDto> result = userService.searchUsers(null, null, null, pageable);

        assertEquals(0, result.getTotalElements());
    }
}
