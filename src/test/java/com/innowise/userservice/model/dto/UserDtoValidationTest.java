package com.innowise.userservice.model.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldInvalidateUserDtoWithFutureBirthdateOrInvalidEmail() {
        UserDto dto = new UserDto(
                "user-1",
                "",
                "",
                LocalDate.now().plusDays(1),
                "invalid-email",
                List.of());
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 3);
    }
}
