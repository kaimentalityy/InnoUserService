package com.innowise.userservice.model.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeycloakTokenResponseTest {

    @Test
    void testKeycloakTokenResponseCreation() {
        KeycloakTokenResponse response = new KeycloakTokenResponse(
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                3600L,
                7200L,
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                "Bearer",
                0,
                "session123",
                "openid profile email");

        assertEquals("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", response.accessToken());
        assertEquals(3600L, response.expiresIn());
        assertEquals(7200L, response.refreshExpiresIn());
        assertEquals("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(0, response.notBeforePolicy());
        assertEquals("session123", response.sessionState());
        assertEquals("openid profile email", response.scope());
    }

    @Test
    void testKeycloakTokenResponseWithNullValues() {
        KeycloakTokenResponse response = new KeycloakTokenResponse(
                null, null, null, null, null, null, null, null);

        assertNull(response.accessToken());
        assertNull(response.expiresIn());
        assertNull(response.refreshExpiresIn());
        assertNull(response.refreshToken());
        assertNull(response.tokenType());
        assertNull(response.notBeforePolicy());
        assertNull(response.sessionState());
        assertNull(response.scope());
    }

    @Test
    void testKeycloakTokenResponseWithEmptyStrings() {
        KeycloakTokenResponse response = new KeycloakTokenResponse(
                "", 0L, 0L, "", "", 0, "", "");

        assertEquals("", response.accessToken());
        assertEquals(0L, response.expiresIn());
        assertEquals(0L, response.refreshExpiresIn());
        assertEquals("", response.refreshToken());
        assertEquals("", response.tokenType());
        assertEquals(0, response.notBeforePolicy());
        assertEquals("", response.sessionState());
        assertEquals("", response.scope());
    }

    @Test
    void testKeycloakTokenResponseWithNegativeValues() {
        KeycloakTokenResponse response = new KeycloakTokenResponse(
                "token", -1L, -1L, "refresh", "Bearer", -1, "session", "scope");

        assertEquals("token", response.accessToken());
        assertEquals(-1L, response.expiresIn());
        assertEquals(-1L, response.refreshExpiresIn());
        assertEquals("refresh", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(-1, response.notBeforePolicy());
        assertEquals("session", response.sessionState());
        assertEquals("scope", response.scope());
    }

    @Test
    void testKeycloakTokenResponseWithZeroValues() {
        KeycloakTokenResponse response = new KeycloakTokenResponse(
                "token", 0L, 0L, "refresh", "Bearer", 0, "session", "scope");

        assertEquals("token", response.accessToken());
        assertEquals(0L, response.expiresIn());
        assertEquals(0L, response.refreshExpiresIn());
        assertEquals("refresh", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(0, response.notBeforePolicy());
        assertEquals("session", response.sessionState());
        assertEquals("scope", response.scope());
    }

    @Test
    void testKeycloakTokenResponseWithLargeValues() {
        KeycloakTokenResponse response = new KeycloakTokenResponse(
                "very-long-access-token-string-that-might-be-used-in-real-scenarios",
                Long.MAX_VALUE,
                Long.MAX_VALUE,
                "very-long-refresh-token-string-that-might-be-used-in-real-scenarios",
                "Bearer",
                Integer.MAX_VALUE,
                "very-long-session-state-string-that-might-be-used-in-real-scenarios",
                "very-long-scope-string-that-might-contain-multiple-scopes-like-openid-profile-email-address-phone");

        assertEquals("very-long-access-token-string-that-might-be-used-in-real-scenarios", response.accessToken());
        assertEquals(Long.MAX_VALUE, response.expiresIn());
        assertEquals(Long.MAX_VALUE, response.refreshExpiresIn());
        assertEquals("very-long-refresh-token-string-that-might-be-used-in-real-scenarios", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(Integer.MAX_VALUE, response.notBeforePolicy());
        assertEquals("very-long-session-state-string-that-might-be-used-in-real-scenarios", response.sessionState());
        assertEquals(
                "very-long-scope-string-that-might-contain-multiple-scopes-like-openid-profile-email-address-phone",
                response.scope());
    }

    @Test
    void testKeycloakTokenResponseWithSpecialCharacters() {
        KeycloakTokenResponse response = new KeycloakTokenResponse(
                "token-with-special-chars-!@#$%^&*()_+-=[]{}|;:,.<>?",
                3600L,
                7200L,
                "refresh-with-special-chars-!@#$%^&*()_+-=[]{}|;:,.<>?",
                "Bearer",
                0,
                "session-with-special-chars-!@#$%^&*()_+-=[]{}|;:,.<>?",
                "scope-with-special-chars-!@#$%^&*()_+-=[]{}|;:,.<>?");

        assertEquals("token-with-special-chars-!@#$%^&*()_+-=[]{}|;:,.<>?", response.accessToken());
        assertEquals(3600L, response.expiresIn());
        assertEquals(7200L, response.refreshExpiresIn());
        assertEquals("refresh-with-special-chars-!@#$%^&*()_+-=[]{}|;:,.<>?", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(0, response.notBeforePolicy());
        assertEquals("session-with-special-chars-!@#$%^&*()_+-=[]{}|;:,.<>?", response.sessionState());
        assertEquals("scope-with-special-chars-!@#$%^&*()_+-=[]{}|;:,.<>?", response.scope());
    }

    @Test
    void testKeycloakTokenResponseWithUnicodeCharacters() {
        KeycloakTokenResponse response = new KeycloakTokenResponse(
                "токен-с-юникодом-привет",
                3600L,
                7200L,
                "обновить-токен-с-юникодом",
                "Bearer",
                0,
                "сессия-с-юникодом",
                "область-с-юникодом");

        assertEquals("токен-с-юникодом-привет", response.accessToken());
        assertEquals(3600L, response.expiresIn());
        assertEquals(7200L, response.refreshExpiresIn());
        assertEquals("обновить-токен-с-юникодом", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(0, response.notBeforePolicy());
        assertEquals("сессия-с-юникодом", response.sessionState());
        assertEquals("область-с-юникодом", response.scope());
    }

    @Test
    void testKeycloakTokenResponseWithDifferentTokenTypes() {
        
        KeycloakTokenResponse bearerResponse = new KeycloakTokenResponse(
                "token", 3600L, 7200L, "refresh", "Bearer", 0, "session", "scope");
        assertEquals("Bearer", bearerResponse.tokenType());

        
        KeycloakTokenResponse lowerBearerResponse = new KeycloakTokenResponse(
                "token", 3600L, 7200L, "refresh", "bearer", 0, "session", "scope");
        assertEquals("bearer", lowerBearerResponse.tokenType());

        
        KeycloakTokenResponse customResponse = new KeycloakTokenResponse(
                "token", 3600L, 7200L, "refresh", "CustomToken", 0, "session", "scope");
        assertEquals("CustomToken", customResponse.tokenType());
    }

    @Test
    void testKeycloakTokenResponseEquality() {
        KeycloakTokenResponse response1 = new KeycloakTokenResponse(
                "same-token", 3600L, 7200L, "same-refresh", "Bearer", 0, "same-session", "same-scope");
        KeycloakTokenResponse response2 = new KeycloakTokenResponse(
                "same-token", 3600L, 7200L, "same-refresh", "Bearer", 0, "same-session", "same-scope");

        assertEquals(response1.accessToken(), response2.accessToken());
        assertEquals(response1.expiresIn(), response2.expiresIn());
        assertEquals(response1.refreshExpiresIn(), response2.refreshExpiresIn());
        assertEquals(response1.refreshToken(), response2.refreshToken());
        assertEquals(response1.tokenType(), response2.tokenType());
        assertEquals(response1.notBeforePolicy(), response2.notBeforePolicy());
        assertEquals(response1.sessionState(), response2.sessionState());
        assertEquals(response1.scope(), response2.scope());
    }

    @Test
    void testKeycloakTokenResponseWithMinimalValues() {
        KeycloakTokenResponse response = new KeycloakTokenResponse(
                "t", 1L, 1L, "r", "B", 1, "s", "s");

        assertEquals("t", response.accessToken());
        assertEquals(1L, response.expiresIn());
        assertEquals(1L, response.refreshExpiresIn());
        assertEquals("r", response.refreshToken());
        assertEquals("B", response.tokenType());
        assertEquals(1, response.notBeforePolicy());
        assertEquals("s", response.sessionState());
        assertEquals("s", response.scope());
    }

    @Test
    void testEqualsAndHashCode() {
        KeycloakTokenResponse res1 = new KeycloakTokenResponse("t", 1L, 1L, "r", "B", 1, "s", "sc");
        KeycloakTokenResponse res2 = new KeycloakTokenResponse("t", 1L, 1L, "r", "B", 1, "s", "sc");
        KeycloakTokenResponse res3 = new KeycloakTokenResponse("t2", 1L, 1L, "r", "B", 1, "s", "sc");

        assertEquals(res1, res2);
        assertNotEquals(res1, res3);
        assertEquals(res1.hashCode(), res2.hashCode());
        assertNotNull(res1.toString());
    }
}
