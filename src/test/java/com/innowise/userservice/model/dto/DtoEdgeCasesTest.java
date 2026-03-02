package com.innowise.userservice.model.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DtoEdgeCasesTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void cardInfoWithBlankHolderShouldFailValidation() {
        CardInfoDto card = new CardInfoDto(
                1L,
                "user-1",
                "1234567890123",
                "",
                LocalDate.now().plusDays(1));

        Set<ConstraintViolation<CardInfoDto>> violations = validator.validate(card);
        assertFalse(violations.isEmpty());
    }

    @Test
    void cardInfoWithNullUserIdShouldFailValidation() {
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
    void userDtoWithNullCardsShouldPassValidation() {
        UserDto user = new UserDto(
                "user-1",
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "john@example.com",
                null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void userDtoWithInvalidEmailShouldFailValidation() {
        UserDto user = new UserDto(
                "user-1",
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "invalid-email",
                List.of());

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void userDtoWithFutureBirthDateShouldFailValidation() {
        UserDto user = new UserDto(
                "user-1",
                "John",
                "Doe",
                LocalDate.now().plusDays(1),
                "john@example.com",
                List.of());

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void errorDtoLombokMethodsShouldWork() {
        ErrorDto error = new ErrorDto();
        error.setMessage("Error message");
        error.setStatus(400);

        assertEquals("Error message", error.getMessage());
        assertEquals(400, error.getStatus());

        ErrorDto error2 = new ErrorDto("Another error", 500);
        assertEquals("Another error", error2.getMessage());
        assertEquals(500, error2.getStatus());
    }
}
