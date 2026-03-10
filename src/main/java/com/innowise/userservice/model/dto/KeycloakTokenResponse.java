package com.innowise.userservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object for Keycloak token response.
 */
public record KeycloakTokenResponse(
                @JsonProperty("access_token") String accessToken,
                @JsonProperty("expires_in") Long expiresIn,
                @JsonProperty("refresh_expires_in") Long refreshExpiresIn,
                @JsonProperty("refresh_token") String refreshToken,
                @JsonProperty("token_type") String tokenType,
                @JsonProperty("not-before-policy") Integer notBeforePolicy,
                @JsonProperty("session_state") String sessionState,
                @JsonProperty("scope") String scope) {
}
