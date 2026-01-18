package com.innowise.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "User Data Transfer Object")
public record UserDto(
        @Schema(description = "Unique identifier of the user", example = "1") Long id,

        @NotBlank @Size(max = 100) @Schema(description = "User's first name", example = "John") String name,

        @NotBlank @Size(max = 100) @Schema(description = "User's last name", example = "Doe") String surname,

        @Past(message = "Birthdate must be in the past") @Schema(description = "User's birth date", example = "1990-01-01") LocalDate birthDate,

        @NotBlank @Email @Size(max = 255) @Schema(description = "User's email address", example = "john.doe@example.com") String email,

        @Schema(description = "User's role", example = "ROLE_USER") String role,

        @Schema(description = "List of user's cards") List<CardInfoDto> cards) {

    public UserDto(Long id, String name, String surname, LocalDate birthDate, String email, List<CardInfoDto> cards) {
        this(id, name, surname, birthDate, email, "ROLE_USER", cards);
    }
}
