package com.innowise.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "User Registration Data Transfer Object")
public record UserRegisterDto(
        @NotBlank(message = "Name is required") @Size(max = 50, message = "Name must be less than 50 characters") @Schema(description = "User's first name", example = "John") String name,

        @NotBlank(message = "Surname is required") @Size(max = 50, message = "Surname must be less than 50 characters") @Schema(description = "User's last name", example = "Doe") String surname,

        @NotNull(message = "Birth date is required") @Past(message = "Birth date must be in the past") @Schema(description = "User's birth date", example = "1990-01-01") LocalDate birthDate,

        @Schema(description = "User's role", example = "USER") String role,

        @NotBlank(message = "Email is required") @Schema(description = "User's email address", example = "john.doe@example.com") String email) {
}
