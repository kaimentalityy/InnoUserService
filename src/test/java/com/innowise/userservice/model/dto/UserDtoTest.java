package com.innowise.userservice.model.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validUserShouldPassValidation() {
        UserDto user = new UserDto(
                "user-1",
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "john.doe@example.com",
                List.of(new CardInfoDto(1L, "user-1", "1234567890123", "John Doe", LocalDate.now().plusDays(1))));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidEmailShouldFailValidation() {
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
    void futureBirthDateShouldFailValidation() {
        UserDto user = new UserDto(
                "user-1",
                "John",
                "Doe",
                LocalDate.now().plusDays(1),
                "john.doe@example.com",
                List.of());

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }
}
