package com.innowise.userservice.model.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CardInfoDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validCardInfoShouldPassValidation() {
        CardInfoDto card = new CardInfoDto(
                1L,
                "user-10",
                "1234567890123",
                "John Doe",
                LocalDate.now().plusDays(1));

        Set<ConstraintViolation<CardInfoDto>> violations = validator.validate(card);
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullUserIdShouldFailValidation() {
        CardInfoDto card = new CardInfoDto(
                1L,
                null,
                "1234567890123",
                "John Doe",
                LocalDate.now().plusDays(1));

        Set<ConstraintViolation<CardInfoDto>> violations = validator.validate(card);
        assertFalse(violations.isEmpty());
    }

    @Test
    void pastExpirationDateShouldFailValidation() {
        CardInfoDto card = new CardInfoDto(
                1L,
                "user-10",
                "1234567890123",
                "John Doe",
                LocalDate.now().minusDays(1));

        Set<ConstraintViolation<CardInfoDto>> violations = validator.validate(card);
        assertFalse(violations.isEmpty());
    }
}
