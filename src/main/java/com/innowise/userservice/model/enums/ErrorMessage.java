package com.innowise.userservice.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Centralized error message constants used across the application.
 */
@Getter
@AllArgsConstructor
public enum ErrorMessage {

    ENTITY_NOT_FOUND("Entity with specified identifier not found"),
    USER_NOT_FOUND("User not found"),
    CARD_NOT_FOUND("Card not found"),

    ENTITY_ALREADY_EXISTS("Entity already exists"),
    EMAIL_ALREADY_EXISTS("Email already exists"),
    CARD_NUMBER_ALREADY_EXISTS("Card number already exists"),

    INVALID_REQUEST("Invalid request parameters"),
    CONSTRAINT_VIOLATION("Constraint violation occurred"),
    INVALID_INPUT("Invalid input data"),

    ACCESS_DENIED("Access denied"),
    INVALID_TOKEN("Invalid or expired JWT token"),
    UNAUTHORIZED_ACCESS("Unauthorized access attempt"),

    INTERNAL_ERROR("An unexpected internal error occurred");

    private final String message;
}
