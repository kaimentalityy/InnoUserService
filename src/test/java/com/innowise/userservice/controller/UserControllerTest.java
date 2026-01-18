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

import java.time.LocalDate;
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
    }

    @Test
    void testCreateUser() {
        UserDto dto = new UserDto(null, "John", "Doe", LocalDate.now().minusYears(20), "john@example.com", "ROLE_USER",
                null);
        UserDto created = new UserDto(1L, "John", "Doe", LocalDate.now().minusYears(20), "john@example.com",
                "ROLE_USER", null);

        when(userService.create(dto)).thenReturn(created);

        ResponseEntity<UserDto> response = userController.createUser(dto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(created, response.getBody());
        verify(userService).create(dto);
    }

    @Test
    void testUpdateUser() {
        UserDto dto = new UserDto(null, "John", "Doe", LocalDate.now().minusYears(20), "john@example.com", "ROLE_USER",
                null);
        UserDto updated = new UserDto(1L, "John", "Doe", LocalDate.now().minusYears(20), "john@example.com",
                "ROLE_USER", null);

        when(userService.update(1L, dto)).thenReturn(updated);

        ResponseEntity<UserDto> response = userController.updateUser(1L, dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updated, response.getBody());
        verify(userService).update(1L, dto);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userService).delete(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(userService).delete(1L);
    }

    @Test
    void testGetUser() {
        UserDto user = new UserDto(1L, "John", "Doe", LocalDate.now().minusYears(20), "john@example.com", "ROLE_USER",
                null);
        when(userService.findById(1L)).thenReturn(user);

        ResponseEntity<UserDto> response = userController.getUser(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
        verify(userService).findById(1L);
    }

    @Test
    void testSearchUsers_byIds() {
        Pageable pageable = Pageable.unpaged();
        UserDto user = new UserDto(1L, "John", "Doe", LocalDate.now().minusYears(20), "john@example.com", "ROLE_USER",
                null);

        when(userService.findByIds(List.of(1L))).thenReturn(List.of(user));

        ResponseEntity<Page<UserDto>> response = userController.searchUsers(List.of(1L), null, null, null, pageable);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(user, response.getBody().getContent().get(0));
        verify(userService).findByIds(List.of(1L));
    }

    @Test
    void testSearchUsers_byEmail() {
        Pageable pageable = Pageable.unpaged();
        UserDto user = new UserDto(1L, "John", "Doe", LocalDate.now().minusYears(20), "john@example.com", "ROLE_USER",
                null);

        when(userService.findByEmail("john@example.com")).thenReturn(user);

        ResponseEntity<Page<UserDto>> response = userController.searchUsers(null, null, null, "john@example.com",
                pageable);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(user, response.getBody().getContent().get(0));
        verify(userService).findByEmail("john@example.com");
    }

    @Test
    void testSearchUsers_standardSearch() {
        Pageable pageable = Pageable.unpaged();
        UserDto user = new UserDto(1L, "John", "Doe", LocalDate.now().minusYears(20), "john@example.com", "ROLE_USER",
                null);
        Page<UserDto> page = new PageImpl<>(List.of(user));

        when(userService.searchUsers("John", "Doe", null, pageable)).thenReturn(page);

        ResponseEntity<Page<UserDto>> response = userController.searchUsers(null, "John", "Doe", null, pageable);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(user, response.getBody().getContent().get(0));
        verify(userService).searchUsers("John", "Doe", null, pageable);
    }

    @Test
    void testSearchUsers_emptyIdsAndEmail_returnsStandardSearch() {
        Pageable pageable = Pageable.unpaged();
        UserDto user = new UserDto(1L, "Jane", "Smith", LocalDate.now().minusYears(25), "jane@example.com", "ROLE_USER",
                null);
        Page<UserDto> page = new PageImpl<>(List.of(user));

        when(userService.searchUsers(null, null, null, pageable)).thenReturn(page);

        ResponseEntity<Page<UserDto>> response = userController.searchUsers(List.of(), null, null, null, pageable);

        assertEquals(200, response.getStatusCodeValue());
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

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(0, response.getBody().getTotalElements());
        verify(userService).findByEmail("unknown@example.com");
    }
}
