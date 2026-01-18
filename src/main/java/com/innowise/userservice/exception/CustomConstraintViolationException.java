package com.innowise.userservice.exception;

import com.innowise.userservice.model.enums.ErrorMessage;

import java.io.Serial;

/**
 * Exception for constraint violation errors (e.g. validation or DB constraints).
 */
public class CustomConstraintViolationException extends ApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CustomConstraintViolationException(String message) {
        super(ErrorMessage.CONSTRAINT_VIOLATION, message);
    }
}
