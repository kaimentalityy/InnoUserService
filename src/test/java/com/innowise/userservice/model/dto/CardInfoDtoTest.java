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

    @Test
    void testAccessors() {
        Long id = 1L;
        String userId = "u1";
        String cardNumber = "123";
        String cardHolder = "H";
        LocalDate expiry = LocalDate.now().plusDays(1);

        CardInfoDto card = new CardInfoDto(id, userId, cardNumber, cardHolder, expiry);

        assertEquals(id, card.id());
        assertEquals(userId, card.userId());
        assertEquals(cardNumber, card.number());
        assertEquals(cardHolder, card.holder());
        assertEquals(expiry, card.expirationDate());
    }

    @Test
    void testEqualsAndHashCode() {
        CardInfoDto card1 = new CardInfoDto(1L, "u1", "123", "H", LocalDate.MAX);
        CardInfoDto card2 = new CardInfoDto(1L, "u1", "123", "H", LocalDate.MAX);
        CardInfoDto card3 = new CardInfoDto(2L, "u1", "123", "H", LocalDate.MAX);

        assertEquals(card1, card2);
        assertNotEquals(card1, card3);
        assertNotEquals(card1, null);
        assertEquals(card1.hashCode(), card2.hashCode());
    }

    @Test
    void testToString() {
        CardInfoDto card = new CardInfoDto(1L, "u1", "123", "H", LocalDate.MAX);
        assertNotNull(card.toString());
        assertTrue(card.toString().contains("u1"));
    }
}
