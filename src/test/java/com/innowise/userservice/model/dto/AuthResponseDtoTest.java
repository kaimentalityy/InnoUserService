package com.innowise.userservice.model.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseDtoTest {

    @Test
    void testAuthResponseDtoCreation() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        AuthResponseDto responseDto = new AuthResponseDto(
                "user123",
                "accessToken123",
                "test@example.com",
                "John",
                "Doe",
                birthDate,
                "refreshToken123");

        assertEquals("user123", responseDto.getId());
        assertEquals("accessToken123", responseDto.getAccessToken());
        assertEquals("test@example.com", responseDto.getEmail());
        assertEquals("John", responseDto.getName());
        assertEquals("Doe", responseDto.getSurname());
        assertEquals(birthDate, responseDto.getBirthDate());
        assertEquals("refreshToken123", responseDto.getRefreshToken());
    }

    @Test
    void testAuthResponseDtoWithNullValues() {
        AuthResponseDto responseDto = new AuthResponseDto(null, null, null, null, null, null, null);

        assertNull(responseDto.getId());
        assertNull(responseDto.getAccessToken());
        assertNull(responseDto.getEmail());
        assertNull(responseDto.getName());
        assertNull(responseDto.getSurname());
        assertNull(responseDto.getBirthDate());
        assertNull(responseDto.getRefreshToken());
    }

    @Test
    void testAuthResponseDtoSettersAndGetters() {
        AuthResponseDto responseDto = new AuthResponseDto(
                "initialId",
                "initialToken",
                "initial@example.com",
                "InitialName",
                "InitialSurname",
                LocalDate.of(1990, 1, 1),
                "initialRefreshToken");

        responseDto.setId("newUserId");
        responseDto.setAccessToken("newAccessToken");
        responseDto.setEmail("new@example.com");
        responseDto.setName("Jane");
        responseDto.setSurname("Smith");
        responseDto.setBirthDate(LocalDate.of(1995, 5, 15));
        responseDto.setRefreshToken("newRefreshToken");

        assertEquals("newUserId", responseDto.getId());
        assertEquals("newAccessToken", responseDto.getAccessToken());
        assertEquals("new@example.com", responseDto.getEmail());
        assertEquals("Jane", responseDto.getName());
        assertEquals("Smith", responseDto.getSurname());
        assertEquals(LocalDate.of(1995, 5, 15), responseDto.getBirthDate());
        assertEquals("newRefreshToken", responseDto.getRefreshToken());
    }

    @Test
    void testAuthResponseDtoWithEmptyStrings() {
        AuthResponseDto responseDto = new AuthResponseDto("", "", "", "", "", null, "");

        assertEquals("", responseDto.getId());
        assertEquals("", responseDto.getAccessToken());
        assertEquals("", responseDto.getEmail());
        assertEquals("", responseDto.getName());
        assertEquals("", responseDto.getSurname());
        assertNull(responseDto.getBirthDate());
        assertEquals("", responseDto.getRefreshToken());
    }

    @Test
    void testAuthResponseDtoWithOnlyId() {
        AuthResponseDto responseDto = new AuthResponseDto(
                "onlyId", null, null, null, null, null, null);

        assertEquals("onlyId", responseDto.getId());
        assertNull(responseDto.getAccessToken());
        assertNull(responseDto.getEmail());
        assertNull(responseDto.getName());
        assertNull(responseDto.getSurname());
        assertNull(responseDto.getBirthDate());
        assertNull(responseDto.getRefreshToken());
    }

    @Test
    void testAuthResponseDtoWithOnlyToken() {
        AuthResponseDto responseDto = new AuthResponseDto(
                null, "onlyToken", null, null, null, null, null);

        assertNull(responseDto.getId());
        assertEquals("onlyToken", responseDto.getAccessToken());
        assertNull(responseDto.getEmail());
        assertNull(responseDto.getName());
        assertNull(responseDto.getSurname());
        assertNull(responseDto.getBirthDate());
        assertNull(responseDto.getRefreshToken());
    }

    @Test
    void testAuthResponseDtoEquality() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        AuthResponseDto dto1 = new AuthResponseDto("id", "token", "email", "name", "surname", birthDate, "refresh");
        AuthResponseDto dto2 = new AuthResponseDto("id", "token", "email", "name", "surname", birthDate, "refresh");

        assertEquals(dto1.getId(), dto2.getId());
        assertEquals(dto1.getAccessToken(), dto2.getAccessToken());
        assertEquals(dto1.getEmail(), dto2.getEmail());
        assertEquals(dto1.getName(), dto2.getName());
        assertEquals(dto1.getSurname(), dto2.getSurname());
        assertEquals(dto1.getBirthDate(), dto2.getBirthDate());
        assertEquals(dto1.getRefreshToken(), dto2.getRefreshToken());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        AuthResponseDto dto1 = new AuthResponseDto("id", "t", "e", "n", "s", birthDate, "r");
        AuthResponseDto dto2 = new AuthResponseDto("id", "t", "e", "n", "s", birthDate, "r");
        AuthResponseDto dto3 = new AuthResponseDto("other", "t", "e", "n", "s", birthDate, "r");

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotNull(dto1.toString());
    }
}
