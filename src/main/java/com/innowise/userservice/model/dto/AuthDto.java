package com.innowise.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Data Transfer Object for authentication requests.
 * Contains user registration and login credentials.
 */
@Schema(description = "Authentication request containing user credentials")
public record AuthDto(

        @NotBlank(message = "Email is required") @Email(message = "Email should be valid") @Schema(description = "User's email address", example = "john.doe@example.com", required = true) String email,

        @NotBlank(message = "Password is required") @Size(min = 8, max = 100, message = "Password must be at least 8 characters long") @Schema(description = "User's password (min 8 characters)", example = "securePassword123", required = true) String password,

        @NotBlank(message = "Name is required") @Size(max = 50, message = "Name must be less than 50 characters") @Schema(description = "User's first name", example = "John", required = true) String name,

        @NotBlank(message = "Surname is required") @Size(max = 50, message = "Surname must be less than 50 characters") @Schema(description = "User's last name", example = "Doe", required = true) String surname,

        @NotNull(message = "Birth date is required") @Past(message = "Birth date must be in the past") @Schema(description = "User's date of birth (YYYY-MM-DD)", example = "1990-01-01", required = true) LocalDate birthDate) {
}
