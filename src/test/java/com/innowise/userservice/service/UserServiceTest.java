package com.innowise.userservice.service;

import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.dto.UserRegisterDto;
import com.innowise.userservice.model.entity.CardInfo;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.dao.UserRepository;
import com.innowise.userservice.service.impl.CardInfoService;
import com.innowise.userservice.service.impl.UserService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

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

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        when(userOperationTimer.record(any(Supplier.class))).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(userOperationTimer).record(any(Runnable.class));
    }

    @Test
    void createUser_success() {
        UserDto dto = new UserDto(null, "John", "Doe", LocalDate.now().minusYears(20), "john@example.com", null);
        User entity = new User();
        User saved = new User();
        saved.setId("a22be142-c4d7-47b1-bef3-f098381b8597");

        when(userMapper.toUser(dto)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toUserDto(saved))
                .thenReturn(new UserDto("a22be142-c4d7-47b1-bef3-f098381b8597", "John", "Doe",
                        LocalDate.now().minusYears(20), "john@example.com", null));

        UserDto result = userService.create(dto);

        assertNotNull(result);
        assertEquals("a22be142-c4d7-47b1-bef3-f098381b8597", result.id());
    }

    @Test
    void findByEmail_success() {
        User user = new User();
        user.setId("a22be142-c4d7-47b1-bef3-f098381b8597");
        user.setEmail("john@example.com");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user))
                .thenReturn(new UserDto("a22be142-c4d7-47b1-bef3-f098381b8597", "John", "Doe",
                        LocalDate.now().minusYears(20), "john@example.com", null));

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
        user.setId("a22be142-c4d7-47b1-bef3-f098381b8597");

        CardInfo card = new CardInfo();
        card.setId(10L);
        user.setCards(List.of(card));

        when(userRepository.findById("a22be142-c4d7-47b1-bef3-f098381b8597")).thenReturn(Optional.of(user));

        userService.delete("a22be142-c4d7-47b1-bef3-f098381b8597");

        verify(cardInfoService, times(1)).evictCardFromCache(10L);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.findById("a22be142-c4d7-47b1-bef3-f098381b8597")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.delete("a22be142-c4d7-47b1-bef3-f098381b8597"));
    }

    @Test
    void findByIds_success() {
        User user = new User();
        user.setId("a22be142-c4d7-47b1-bef3-f098381b8597");

        when(userRepository.findAllById(List.of("a22be142-c4d7-47b1-bef3-f098381b8597"))).thenReturn(List.of(user));
        when(userMapper.toUserDto(user))
                .thenReturn(new UserDto("a22be142-c4d7-47b1-bef3-f098381b8597", "John", "Doe",
                        LocalDate.now().minusYears(20), "john@example.com", null));

        var list = userService.findByIds(List.of("a22be142-c4d7-47b1-bef3-f098381b8597"));

        assertEquals(1, list.size());
        assertEquals("a22be142-c4d7-47b1-bef3-f098381b8597", list.get(0).id());
    }

    @Test
    void createFromAuth_success() {
        UserRegisterDto registerDto = new UserRegisterDto(
                "John", "Doe", LocalDate.now().minusYears(20), "john@example.com");

        User saved = new User();
        saved.setId("a22be142-c4d7-47b1-bef3-f098381b8597");
        saved.setName("John");
        saved.setSurname("Doe");

        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(userMapper.toUserDto(saved)).thenReturn(
                new UserDto("a22be142-c4d7-47b1-bef3-f098381b8597", "John", "Doe", LocalDate.now().minusYears(20),
                        "john@example.com", null));

        UserDto result = userService.createFromAuth(registerDto, "a22be142-c4d7-47b1-bef3-f098381b8597");

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

}
