package com.innowise.userservice.exception;

import com.innowise.userservice.model.enums.ErrorMessage;

import java.io.Serial;

/**
 * Exception thrown when access is denied.
 */
public class AccessDeniedCustomException extends ApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public AccessDeniedCustomException() {
        super(ErrorMessage.ACCESS_DENIED);
    }
}
