package com.innowise.userservice.model.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AuthDtoTest {

    @Test
    void testAuthDtoCreation() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        AuthDto authDto = new AuthDto("test@example.com", "password123", "John", "Doe", birthDate);

        assertEquals("test@example.com", authDto.email());
        assertEquals("password123", authDto.password());
        assertEquals("John", authDto.name());
        assertEquals("Doe", authDto.surname());
        assertEquals(birthDate, authDto.birthDate());
    }

    @Test
    void testAuthDtoWithNullValues() {
        
        assertDoesNotThrow(() -> new AuthDto(null, null, null, null, null));

        
        AuthDto authDto = new AuthDto(null, null, null, null, null);
        assertNull(authDto.email());
        assertNull(authDto.password());
        assertNull(authDto.name());
        assertNull(authDto.surname());
        assertNull(authDto.birthDate());
    }

    @Test
    void testAuthDtoWithEmptyEmail() {
        assertDoesNotThrow(() -> new AuthDto("", "password123", "John", "Doe", LocalDate.now()));
    }

    @Test
    void testAuthDtoWithShortPassword() {
        assertDoesNotThrow(() -> new AuthDto("test@example.com", "short", "John", "Doe", LocalDate.now()));
    }

    @Test
    void testAuthDtoWithFutureBirthDate() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        assertDoesNotThrow(() -> new AuthDto("test@example.com", "password123", "John", "Doe", futureDate));
    }

    @Test
    void testAuthDtoWithLongName() {
        String longName = "a".repeat(51);
        assertDoesNotThrow(() -> new AuthDto("test@example.com", "password123", longName, "Doe", LocalDate.now()));
    }

    @Test
    void testAuthDtoWithLongSurname() {
        String longSurname = "a".repeat(51);
        assertDoesNotThrow(() -> new AuthDto("test@example.com", "password123", "John", longSurname, LocalDate.now()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "valid@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org",
            "user123@test-domain.com"
    })
    void testAuthDtoWithValidEmails(String email) {
        assertDoesNotThrow(() -> new AuthDto(email, "password123", "John", "Doe", LocalDate.now()));
    }

    @Test
    void testAuthDtoWithSpecialCharactersInPassword() {
        String specialPassword = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        assertDoesNotThrow(() -> new AuthDto("test@example.com", specialPassword, "John", "Doe", LocalDate.now()));
    }

    @Test
    void testAuthDtoWithUnicodeCharacters() {
        assertDoesNotThrow(() -> new AuthDto("tëst@éxample.com", "pássword123", "Jöhn", "Döe", LocalDate.now()));
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        AuthDto auth1 = new AuthDto("test@example.com", "pass", "John", "Doe", birthDate);
        AuthDto auth2 = new AuthDto("test@example.com", "pass", "John", "Doe", birthDate);
        AuthDto auth3 = new AuthDto("other@example.com", "pass", "John", "Doe", birthDate);

        assertEquals(auth1, auth2);
        assertNotEquals(auth1, auth3);
        assertNotEquals(auth1, null);
        assertNotEquals(auth1, "not an auth");
        assertEquals(auth1.hashCode(), auth2.hashCode());
        assertNotEquals(auth1.hashCode(), auth3.hashCode());
    }

    @Test
    void testToString() {
        AuthDto auth = new AuthDto("test@example.com", "pass", "John", "Doe", LocalDate.of(1990, 1, 1));
        assertNotNull(auth.toString());
        assertTrue(auth.toString().contains("test@example.com"));
    }
}
