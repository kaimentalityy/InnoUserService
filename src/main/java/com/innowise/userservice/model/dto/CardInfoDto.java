package com.innowise.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Card Information Data Transfer Object")
public record CardInfoDto(
        @Schema(description = "Unique identifier of the card", example = "1") Long id,

        @NotNull @Schema(description = "ID of the user who owns the card", example = "a22be142-c4d7-47b1-bef3-f098381b8597") String userId,

        @NotBlank @Size(max = 20) @Schema(description = "Card number", example = "1234-5678-9012-3456") String number,

        @NotBlank @Size(max = 150) @Schema(description = "Card holder name", example = "JOHN DOE") String holder,

        @NotNull @FutureOrPresent(message = "Expiration date must be today or in the future") @Schema(description = "Card expiration date", example = "2025-12-31") LocalDate expirationDate) {
}
