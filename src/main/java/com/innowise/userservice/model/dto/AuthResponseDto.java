package com.innowise.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object for authentication responses.
 * Contains the JWT token, username, and refresh token.
 */
@Data
@AllArgsConstructor
@Schema(description = "Authentication response containing JWT tokens and user info")
public class AuthResponseDto {

    @Schema(description = "Authenticated user's ID", example = "a22be142-c4d7-47b1-bef3-f098381b8597")
    private String id;

    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Authenticated user's email", example = "john@example.com")
    private String email;

    @Schema(description = "Authenticated user's name", example = "John")
    private String name;

    @Schema(description = "Authenticated user's surname", example = "Doe")
    private String surname;

    @Schema(description = "Authenticated user's birth date", example = "1990-01-01")
    private java.time.LocalDate birthDate;

    @Schema(description = "Refresh token used to obtain a new access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
}
