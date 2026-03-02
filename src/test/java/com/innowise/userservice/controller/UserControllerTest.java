package com.innowise.userservice.controller;

import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.service.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateUser() {
        UserDto dto = new UserDto(null, "John", "Doe", LocalDate.now().minusYears(20), "john@example.com", null);
        UserDto created = new UserDto("a22be142-c4d7-47b1-bef3-f098381b8597", "John", "Doe",
                LocalDate.now().minusYears(20), "john@example.com",
                null);

        when(userService.create(dto)).thenReturn(created);

        ResponseEntity<UserDto> response = userController.createUser(dto);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(created, response.getBody());
        verify(userService).create(dto);
    }

    @Test
    void testUpdateUser() {
        UserDto dto = new UserDto(null, "John", "Doe", LocalDate.now().minusYears(20), "john@example.com", null);
        UserDto updated = new UserDto("a22be142-c4d7-47b1-bef3-f098381b8597", "John", "Doe",
                LocalDate.now().minusYears(20), "john@example.com",
                null);

        when(userService.update("a22be142-c4d7-47b1-bef3-f098381b8597", dto)).thenReturn(updated);

        ResponseEntity<UserDto> response = userController.updateUser("a22be142-c4d7-47b1-bef3-f098381b8597", dto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(updated, response.getBody());
        verify(userService).update("a22be142-c4d7-47b1-bef3-f098381b8597", dto);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userService).delete("a22be142-c4d7-47b1-bef3-f098381b8597");

        ResponseEntity<Void> response = userController.deleteUser("a22be142-c4d7-47b1-bef3-f098381b8597");

        assertEquals(204, response.getStatusCode().value());
        verify(userService).delete("a22be142-c4d7-47b1-bef3-f098381b8597");
    }

    @Test
    void testGetUser() {
        UserDto user = new UserDto("a22be142-c4d7-47b1-bef3-f098381b8597", "John", "Doe",
                LocalDate.now().minusYears(20), "john@example.com", null);
        when(userService.findById("a22be142-c4d7-47b1-bef3-f098381b8597")).thenReturn(user);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        when(auth.getName()).thenReturn("admin@example.com");
        when(auth.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        ResponseEntity<UserDto> response = userController.getUser("a22be142-c4d7-47b1-bef3-f098381b8597");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(user, response.getBody());
        verify(userService).findById("a22be142-c4d7-47b1-bef3-f098381b8597");
    }

    @Test
    void testSearchUsers_byIds() {
        Pageable pageable = Pageable.unpaged();
        UserDto user = new UserDto("a22be142-c4d7-47b1-bef3-f098381b8597", "John", "Doe",
                LocalDate.now().minusYears(20), "john@example.com", null);

        when(userService.findByIds(List.of("a22be142-c4d7-47b1-bef3-f098381b8597"))).thenReturn(List.of(user));

        ResponseEntity<Page<UserDto>> response = userController
                .searchUsers(List.of("a22be142-c4d7-47b1-bef3-f098381b8597"), null, null, null, pageable);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(user, response.getBody().getContent().get(0));
        verify(userService).findByIds(List.of("a22be142-c4d7-47b1-bef3-f098381b8597"));
    }

    @Test
    void testSearchUsers_byEmail() {
        Pageable pageable = Pageable.unpaged();
        UserDto user = new UserDto("a22be142-c4d7-47b1-bef3-f098381b8597", "John", "Doe",
                LocalDate.now().minusYears(20), "john@example.com", null);

        when(userService.findByEmail("john@example.com")).thenReturn(user);

        ResponseEntity<Page<UserDto>> response = userController.searchUsers(null, null, null, "john@example.com",
                pageable);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(user, response.getBody().getContent().get(0));
        verify(userService).findByEmail("john@example.com");
    }

    @Test
    void testSearchUsers_standardSearch() {
        Pageable pageable = Pageable.unpaged();
        UserDto user = new UserDto("a22be142-c4d7-47b1-bef3-f098381b8597", "John", "Doe",
                LocalDate.now().minusYears(20), "john@example.com", null);
        Page<UserDto> page = new PageImpl<>(List.of(user));

        when(userService.searchUsers("John", "Doe", null, pageable)).thenReturn(page);

        ResponseEntity<Page<UserDto>> response = userController.searchUsers(null, "John", "Doe", null, pageable);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(user, response.getBody().getContent().get(0));
        verify(userService).searchUsers("John", "Doe", null, pageable);
    }

    @Test
    void testSearchUsers_emptyIdsAndEmail_returnsStandardSearch() {
        Pageable pageable = Pageable.unpaged();
        UserDto user = new UserDto("a22be142-c4d7-47b1-bef3-f098381b8597", "Jane", "Smith",
                LocalDate.now().minusYears(25), "jane@example.com", null);
        Page<UserDto> page = new PageImpl<>(List.of(user));

        when(userService.searchUsers(null, null, null, pageable)).thenReturn(page);

        ResponseEntity<Page<UserDto>> response = userController.searchUsers(List.of(), null, null, null, pageable);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(user, response.getBody().getContent().get(0));
        verify(userService).searchUsers(null, null, null, pageable);
    }

    @Test
    void testSearchUsers_emailNotFound_returnsEmptyPage() {
        Pageable pageable = Pageable.unpaged();

        when(userService.findByEmail("unknown@example.com")).thenReturn(null);

        ResponseEntity<Page<UserDto>> response = userController.searchUsers(null, null, null, "unknown@example.com",
                pageable);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(0, response.getBody().getTotalElements());
        verify(userService).findByEmail("unknown@example.com");
    }
}
