package com.innowise.userservice.model.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class CardInfoDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateCorrectCardInfoDto() {
        CardInfoDto dto = new CardInfoDto(
                1L,
                "user-1",
                "1234567890123",
                "John Doe",
                LocalDate.now().plusDays(1));
        Set<ConstraintViolation<CardInfoDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldInvalidateCardNumberAndExpirationDate() {
        CardInfoDto dto = new CardInfoDto(
                1L,
                "user-1",
                "badnumber",
                "John Doe",
                LocalDate.now().minusDays(1));
        Set<ConstraintViolation<CardInfoDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
    }
}
