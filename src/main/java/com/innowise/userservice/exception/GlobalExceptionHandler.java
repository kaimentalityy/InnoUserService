package com.innowise.userservice.exception;

import com.innowise.userservice.model.dto.ErrorDto;
import com.innowise.userservice.model.enums.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler.
 * Handles all custom application exceptions and validation errors,
 * returning consistent {@link ErrorDto} responses with appropriate HTTP status
 * codes.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles all custom {@link ApplicationException} types.
     */
    @ExceptionHandler(ApplicationException.class)
    public ErrorDto handleApplicationException(ApplicationException e) {
        HttpStatus status = mapStatus(e);
        log.warn("{}: {}", status, e.getMessage());
        return buildErrorDto(e.getMessage(), status);
    }

    /**
     * Handles validation errors from @Valid annotated requests.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((m1, m2) -> m1 + "; " + m2)
                .orElse("Validation failed");

        log.warn("Validation error: {}", message);
        return new ErrorDto(message, HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Handles security-related exceptions.
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDto handleAccessDenied(org.springframework.security.access.AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        return new ErrorDto("Access Denied", HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDto handleAuthenticationException(org.springframework.security.core.AuthenticationException e) {
        log.warn("Authentication failed: {}", e.getMessage());
        return new ErrorDto("Authentication Failed", HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * Handles unexpected exceptions (fallback).
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleUnexpected(Exception e) {
        log.error("Unexpected error: ", e);
        return new ErrorDto(ErrorMessage.INTERNAL_ERROR.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Maps exception type to HTTP status.
     */
    private HttpStatus mapStatus(ApplicationException ex) {
        ErrorMessage errorMessage = ex.getErrorMessage();

        if (errorMessage == ErrorMessage.ENTITY_ALREADY_EXISTS) {
            return HttpStatus.CONFLICT;
        } else if (errorMessage == ErrorMessage.ENTITY_NOT_FOUND) {
            return HttpStatus.NOT_FOUND;
        } else if (errorMessage == ErrorMessage.INVALID_REQUEST) {
            return HttpStatus.BAD_REQUEST;
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private ErrorDto buildErrorDto(String message, HttpStatus status) {
        return new ErrorDto(message, status.value());
    }
}
