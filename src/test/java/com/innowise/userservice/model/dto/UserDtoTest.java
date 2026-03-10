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

    @Test
    void testAccessors() {
        String id = "user-1";
        String name = "John";
        String surname = "Doe";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        String email = "john.doe@example.com";
        List<CardInfoDto> cards = List.of();

        UserDto user = new UserDto(id, name, surname, birthDate, email, cards);

        assertEquals(id, user.id());
        assertEquals(name, user.name());
        assertEquals(surname, user.surname());
        assertEquals(birthDate, user.birthDate());
        assertEquals(email, user.email());
        assertEquals(cards, user.cards());
    }

    @Test
    void testEqualsAndHashCode() {
        UserDto user1 = new UserDto("1", "N", "S", LocalDate.of(1990, 1, 1), "e@e.com", List.of());
        UserDto user2 = new UserDto("1", "N", "S", LocalDate.of(1990, 1, 1), "e@e.com", List.of());
        UserDto user3 = new UserDto("2", "N", "S", LocalDate.of(1990, 1, 1), "e@e.com", List.of());

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertNotEquals(user1, null);
        assertNotEquals(user1, "not a user");
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void testToString() {
        UserDto user = new UserDto("1", "N", "S", LocalDate.of(1990, 1, 1), "e@e.com", List.of());
        assertNotNull(user.toString());
        assertTrue(user.toString().contains("1"));
        assertTrue(user.toString().contains("N"));
    }
}
