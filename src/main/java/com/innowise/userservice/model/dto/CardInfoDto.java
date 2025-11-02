package com.innowise.userservice.model.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CardInfoDto(
        Long id,

        @NotNull
        Long userId,

        @NotBlank
        @Size(max = 20)
        String number,

        @NotBlank
        @Size(max = 150)
        String holder,

        @NotNull
        @FutureOrPresent(message = "Expiration date must be today or in the future")
        LocalDate expirationDate
) {}
