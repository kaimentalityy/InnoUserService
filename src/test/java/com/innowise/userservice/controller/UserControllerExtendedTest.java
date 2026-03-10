package com.innowise.userservice.controller;

import com.innowise.userservice.model.dto.AuthDto;
import com.innowise.userservice.model.dto.AuthResponseDto;
import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.service.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerExtendedTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        
    }

    @Test
    void registerFromAuth_success_returns201() {
        AuthDto dto = new AuthDto("john@example.com", "Password1!", "John", "Doe",
                LocalDate.of(1990, 1, 1));
        AuthResponseDto responseDto = new AuthResponseDto(
                "kc-uuid-1", null, "john@example.com", "John", "Doe", LocalDate.of(1990, 1, 1), null);

        when(userService.register(dto)).thenReturn(responseDto);

        ResponseEntity<AuthResponseDto> response = userController.registerFromAuth(dto);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("john@example.com", response.getBody().getEmail());
        assertNull(response.getBody().getAccessToken());
        verify(userService).register(dto);
    }

    @Test
    void deleteByEmailInternal_callsServiceAndReturns204() {
        doNothing().when(userService).deleteByEmail("test@example.com");

        ResponseEntity<Void> response = userController.deleteByEmailInternal("test@example.com");

        assertEquals(204, response.getStatusCode().value());
        verify(userService).deleteByEmail("test@example.com");
    }

    @Test
    void getUserInternal_returnsUserDto() {
        String id = "uuid-internal-1";
        UserDto dto = new UserDto(id, "Internal", "User", LocalDate.of(1988, 6, 15), "internal@example.com", null);
        when(userService.findById(id)).thenReturn(dto);

        ResponseEntity<UserDto> response = userController.getUserInternal(id);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(id, response.getBody().id());
    }

    @Test
    void getUserByParametersInternal_withEmail_returnsUser() {
        UserDto dto = new UserDto("x", "A", "B", LocalDate.of(1990, 1, 1), "a@b.com", null);
        when(userService.findByEmail("a@b.com")).thenReturn(dto);

        ResponseEntity<UserDto> response = userController.getUserByParametersInternal("a@b.com");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("a@b.com", response.getBody().email());
    }

    @Test
    void getUserByParametersInternal_withoutEmail_returns400() {
        ResponseEntity<UserDto> response = userController.getUserByParametersInternal(null);

        assertEquals(400, response.getStatusCode().value());
        verifyNoInteractions(userService);
    }

    @Test
    void getUserByEmailInternal_returnsUser() {
        String email = "hello@example.com";
        UserDto dto = new UserDto("y", "H", "I", LocalDate.of(1995, 7, 20), email, null);
        when(userService.findByEmail(email)).thenReturn(dto);

        ResponseEntity<UserDto> response = userController.getUserByEmailInternal(email);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(email, response.getBody().email());
    }
}
