package com.innowise.userservice.controller;

import com.innowise.userservice.model.dto.UserRegisterDto;
import com.innowise.userservice.service.impl.UserService;
import com.innowise.userservice.model.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "API for managing users")
public class UserController {

    private final UserService userService;

    /**
     * Accessible only by ADMIN
     */
    @Operation(summary = "Create a new user", description = "Accessible only by ADMIN")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
    }

    /**
     * Create a user from AuthService registration
     * Accessible internally via AuthService call
     */
    @Operation(summary = "Register a user from AuthService", description = "Accessible internally via AuthService call")
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerFromAuth(@Valid @RequestBody UserRegisterDto dto) {
        UserDto createdUser = userService.createFromAuth(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Accessible only by ADMIN
     */
    @Operation(summary = "Update an existing user", description = "Accessible only by ADMIN")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    /**
     * Accessible only by ADMIN
     */
    @Operation(summary = "Delete a user", description = "Accessible only by ADMIN")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Accessible by ADMIN or USER (but USER can only view their own data)
     */
    @Operation(summary = "Get user by ID", description = "Accessible by ADMIN or USER (but USER can only view their own data)")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(summary = "Delete user by email (Internal)", description = "Accessible internally")
    @DeleteMapping("/internal/{email}")
    public ResponseEntity<Void> deleteByEmailInternal(@PathVariable String email) {
        userService.deleteByEmail(email);
        return ResponseEntity.noContent().build();
    }

    /**
     * Accessible only by ADMIN
     */
    @Operation(summary = "Search users", description = "Accessible only by ADMIN")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserDto>> searchUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @RequestParam(required = false) String email,
            Pageable pageable) {

        if (ids != null && !ids.isEmpty()) {
            List<UserDto> results = userService.findByIds(ids);
            Page<UserDto> page = PageableExecutionUtils.getPage(results, pageable, results::size);
            return ResponseEntity.ok(page);
        }

        if (email != null) {
            UserDto user = userService.findByEmail(email);
            List<UserDto> results = user != null ? List.of(user) : List.of();
            Page<UserDto> page = PageableExecutionUtils.getPage(results, pageable, results::size);
            return ResponseEntity.ok(page);
        }

        return ResponseEntity.ok(userService.searchUsers(name, surname, email, pageable));
    }
}
