package com.innowise.userservice.model.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void userDto_valid_shouldHaveNoViolations() {
        UserDto dto = new UserDto(
                1L,
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "john@example.com",
                List.of(new CardInfoDto(1L, 1L, "1234567890123", "John Doe", LocalDate.now().plusDays(1)))
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void userDto_invalid_shouldDetectViolations() {
        UserDto dto = new UserDto(
                1L,
                "",
                "Doe",
                LocalDate.now().plusDays(1),
                "invalid-email",
                List.of()
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals(3, violations.size());
    }

    @Test
    void cardInfoDto_valid_shouldHaveNoViolations() {
        CardInfoDto dto = new CardInfoDto(
                1L,
                1L,
                "1234567890123",
                "John Doe",
                LocalDate.now().plusDays(1)
        );
        Set<ConstraintViolation<CardInfoDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void cardInfoDto_invalid_shouldDetectViolations() {
        CardInfoDto dto = new CardInfoDto(
                1L,
                null,
                "12",
                "",
                LocalDate.now().minusDays(1)
        );
        Set<ConstraintViolation<CardInfoDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals(3, violations.size());
    }
}
