package com.innowise.userservice.service;

import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.dto.UserRegisterDto;
import com.innowise.userservice.model.entity.CardInfo;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.model.enums.UserRole;
import com.innowise.userservice.repository.dao.UserRepository;
import com.innowise.userservice.service.impl.CardInfoService;
import com.innowise.userservice.service.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private CardInfoService cardInfoService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_success() {
        UserDto dto = new UserDto(null, "John", "Doe", LocalDate.now().minusYears(20), "john@example.com", null);
        User entity = new User();
        User saved = new User();
        saved.setId(1L);

        when(userMapper.toUser(dto)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toUserDto(saved))
                .thenReturn(new UserDto(1L, "John", "Doe", LocalDate.now().minusYears(20), "john@example.com", null));

        UserDto result = userService.create(dto);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void findByEmail_success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user))
                .thenReturn(new UserDto(1L, "John", "Doe", LocalDate.now().minusYears(20), "john@example.com", null));

        UserDto dto = userService.findByEmail("john@example.com");

        assertNotNull(dto);
        assertEquals("john@example.com", dto.email());
    }

    @Test
    void findByEmail_notFound() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.findByEmail("john@example.com"));
    }

    @Test
    void deleteUser_success() {
        User user = new User();
        user.setId(1L);

        CardInfo card = new CardInfo();
        card.setId(10L);
        user.setCards(List.of(card));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(cardInfoService, times(1)).evictCardFromCache(10L);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.delete(1L));
    }

    @Test
    void findByIds_success() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findAllById(List.of(1L))).thenReturn(List.of(user));
        when(userMapper.toUserDto(user))
                .thenReturn(new UserDto(1L, "John", "Doe", LocalDate.now().minusYears(20), "john@example.com", null));

        var list = userService.findByIds(List.of(1L));

        assertEquals(1, list.size());
        assertEquals(1L, list.get(0).id());
    }

    @Test
    void createFromAuth_nullRole_defaultsToUser() {
        UserRegisterDto registerDto = new UserRegisterDto(
                "John", "Doe", LocalDate.now().minusYears(20), null, "john@example.com");

        User saved = new User();
        saved.setId(1L);
        saved.setName("John");
        saved.setSurname("Doe");
        saved.setRole(UserRole.ROLE_USER);

        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(userMapper.toUserDto(saved)).thenReturn(
                new UserDto(1L, "John", "Doe", LocalDate.now().minusYears(20), "john@example.com", "ROLE_USER", null));

        UserDto result = userService.createFromAuth(registerDto);

        assertNotNull(result);
        assertEquals("ROLE_USER", result.role());
        verify(userRepository)
                .save(argThat(user -> user.getRole() == UserRole.ROLE_USER));
    }

    @Test
    void createFromAuth_blankRole_defaultsToUser() {
        UserRegisterDto registerDto = new UserRegisterDto(
                "John", "Doe", LocalDate.now().minusYears(20), "  ", "john@example.com");

        User saved = new User();
        saved.setId(1L);
        saved.setName("John");
        saved.setSurname("Doe");
        saved.setRole(UserRole.ROLE_USER);

        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(userMapper.toUserDto(saved)).thenReturn(
                new UserDto(1L, "John", "Doe", LocalDate.now().minusYears(20), "john@example.com", "ROLE_USER", null));

        UserDto result = userService.createFromAuth(registerDto);

        assertNotNull(result);
        assertEquals("ROLE_USER", result.role());
        verify(userRepository)
                .save(argThat(user -> user.getRole() == UserRole.ROLE_USER));
    }
}
