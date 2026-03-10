package com.innowise.userservice.model.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserRegisterDtoTest {

    @Test
    void testUserRegisterDtoCreation() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        UserRegisterDto dto = new UserRegisterDto("John", "Doe", birthDate, "john@example.com");

        assertEquals("John", dto.name());
        assertEquals("Doe", dto.surname());
        assertEquals(birthDate, dto.birthDate());
        assertEquals("john@example.com", dto.email());
    }

    @Test
    void testUserRegisterDtoWithNullValues() {
        
        assertDoesNotThrow(() -> new UserRegisterDto(null, null, null, null));

        
        UserRegisterDto dto = new UserRegisterDto(null, null, null, null);
        assertNull(dto.name());
        assertNull(dto.surname());
        assertNull(dto.birthDate());
        assertNull(dto.email());
    }

    @Test
    void testUserRegisterDtoWithEmptyStrings() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        UserRegisterDto dto = new UserRegisterDto("", "", birthDate, "");

        assertEquals("", dto.name());
        assertEquals("", dto.surname());
        assertEquals(birthDate, dto.birthDate());
        assertEquals("", dto.email());
    }

    @Test
    void testUserRegisterDtoWithFutureBirthDate() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        assertDoesNotThrow(() -> new UserRegisterDto("John", "Doe", futureDate, "john@example.com"));

        UserRegisterDto dto = new UserRegisterDto("John", "Doe", futureDate, "john@example.com");
        assertEquals(futureDate, dto.birthDate());
    }

    @Test
    void testUserRegisterDtoWithPastBirthDate() {
        LocalDate pastDate = LocalDate.now().minusYears(25);
        assertDoesNotThrow(() -> new UserRegisterDto("John", "Doe", pastDate, "john@example.com"));

        UserRegisterDto dto = new UserRegisterDto("John", "Doe", pastDate, "john@example.com");
        assertEquals(pastDate, dto.birthDate());
    }

    @Test
    void testUserRegisterDtoWithTodayBirthDate() {
        LocalDate today = LocalDate.now();
        assertDoesNotThrow(() -> new UserRegisterDto("John", "Doe", today, "john@example.com"));

        UserRegisterDto dto = new UserRegisterDto("John", "Doe", today, "john@example.com");
        assertEquals(today, dto.birthDate());
    }

    @Test
    void testUserRegisterDtoWithLongNames() {
        String longName = "a".repeat(50);
        String longSurname = "a".repeat(50);
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        assertDoesNotThrow(() -> new UserRegisterDto(longName, longSurname, birthDate, "test@example.com"));

        UserRegisterDto dto = new UserRegisterDto(longName, longSurname, birthDate, "test@example.com");
        assertEquals(longName, dto.name());
        assertEquals(longSurname, dto.surname());
    }

    @Test
    void testUserRegisterDtoWithNamesOverLimit() {
        String tooLongName = "a".repeat(51);
        String tooLongSurname = "a".repeat(51);
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        assertDoesNotThrow(() -> new UserRegisterDto(tooLongName, tooLongSurname, birthDate, "test@example.com"));

        UserRegisterDto dto = new UserRegisterDto(tooLongName, tooLongSurname, birthDate, "test@example.com");
        assertEquals(tooLongName, dto.name());
        assertEquals(tooLongSurname, dto.surname());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "valid@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org",
            "user123@test-domain.com",
            "test.email.with+symbol@example.com",
            "user@sub.domain.com",
            "user@domain-with-hyphen.com"
    })
    void testUserRegisterDtoWithValidEmails(String email) {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        assertDoesNotThrow(() -> new UserRegisterDto("John", "Doe", birthDate, email));

        UserRegisterDto dto = new UserRegisterDto("John", "Doe", birthDate, email);
        assertEquals(email, dto.email());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-email",
            "@domain.com",
            "user@",
            "user..name@domain.com",
            "user@.domain.com",
            "user@domain.",
            "user name@domain.com",
            "user@domain..com"
    })
    void testUserRegisterDtoWithInvalidEmails(String email) {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        assertDoesNotThrow(() -> new UserRegisterDto("John", "Doe", birthDate, email));

        UserRegisterDto dto = new UserRegisterDto("John", "Doe", birthDate, email);
        assertEquals(email, dto.email());
    }

    @Test
    void testUserRegisterDtoWithSpecialCharactersInNames() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        assertDoesNotThrow(() -> new UserRegisterDto("John-Paul", "O'Connor-Smith", birthDate, "test@example.com"));

        UserRegisterDto dto = new UserRegisterDto("John-Paul", "O'Connor-Smith", birthDate, "test@example.com");
        assertEquals("John-Paul", dto.name());
        assertEquals("O'Connor-Smith", dto.surname());
    }

    @Test
    void testUserRegisterDtoWithUnicodeCharacters() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        assertDoesNotThrow(() -> new UserRegisterDto("Джон", "Доу", birthDate, "john@example.com"));

        UserRegisterDto dto = new UserRegisterDto("Джон", "Доу", birthDate, "john@example.com");
        assertEquals("Джон", dto.name());
        assertEquals("Доу", dto.surname());
    }

    @Test
    void testUserRegisterDtoWithUnicodeEmail() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        assertDoesNotThrow(() -> new UserRegisterDto("John", "Doe", birthDate, "john@пример.com"));

        UserRegisterDto dto = new UserRegisterDto("John", "Doe", birthDate, "john@пример.com");
        assertEquals("john@пример.com", dto.email());
    }

    @Test
    void testUserRegisterDtoWithNumbersInNames() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        assertDoesNotThrow(() -> new UserRegisterDto("John123", "Doe456", birthDate, "test@example.com"));

        UserRegisterDto dto = new UserRegisterDto("John123", "Doe456", birthDate, "test@example.com");
        assertEquals("John123", dto.name());
        assertEquals("Doe456", dto.surname());
    }

    @Test
    void testUserRegisterDtoWithVeryOldBirthDate() {
        LocalDate veryOldDate = LocalDate.of(1900, 1, 1);
        assertDoesNotThrow(() -> new UserRegisterDto("John", "Doe", veryOldDate, "test@example.com"));

        UserRegisterDto dto = new UserRegisterDto("John", "Doe", veryOldDate, "test@example.com");
        assertEquals(veryOldDate, dto.birthDate());
    }

    @Test
    void testUserRegisterDtoWithEdgeCaseBirthDates() {
        
        LocalDate minDate = LocalDate.MIN;
        assertDoesNotThrow(() -> new UserRegisterDto("John", "Doe", minDate, "test@example.com"));

        UserRegisterDto dto1 = new UserRegisterDto("John", "Doe", minDate, "test@example.com");
        assertEquals(minDate, dto1.birthDate());

        
        LocalDate maxDate = LocalDate.MAX;
        assertDoesNotThrow(() -> new UserRegisterDto("Jane", "Smith", maxDate, "test@example.com"));

        UserRegisterDto dto2 = new UserRegisterDto("Jane", "Smith", maxDate, "test@example.com");
        assertEquals(maxDate, dto2.birthDate());
    }

    @Test
    void testUserRegisterDtoEquality() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        UserRegisterDto dto1 = new UserRegisterDto("John", "Doe", birthDate, "john@example.com");
        UserRegisterDto dto2 = new UserRegisterDto("John", "Doe", birthDate, "john@example.com");

        assertEquals(dto1.name(), dto2.name());
        assertEquals(dto1.surname(), dto2.surname());
        assertEquals(dto1.birthDate(), dto2.birthDate());
        assertEquals(dto1.email(), dto2.email());
    }

    @Test
    void testUserRegisterDtoWithMinimalValues() {
        LocalDate birthDate = LocalDate.of(2000, 1, 1);
        UserRegisterDto dto = new UserRegisterDto("A", "B", birthDate, "a@b.c");

        assertEquals("A", dto.name());
        assertEquals("B", dto.surname());
        assertEquals(birthDate, dto.birthDate());
        assertEquals("a@b.c", dto.email());
    }

    @Test
    void testUserRegisterDtoWithMixedCaseEmail() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        UserRegisterDto dto = new UserRegisterDto("John", "Doe", birthDate, "John.Doe@EXAMPLE.COM");

        assertEquals("John.Doe@EXAMPLE.COM", dto.email());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        UserRegisterDto dto1 = new UserRegisterDto("John", "Doe", birthDate, "j@e.com");
        UserRegisterDto dto2 = new UserRegisterDto("John", "Doe", birthDate, "j@e.com");
        UserRegisterDto dto3 = new UserRegisterDto("Jane", "Doe", birthDate, "j@e.com");

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotNull(dto1.toString());
    }
}
