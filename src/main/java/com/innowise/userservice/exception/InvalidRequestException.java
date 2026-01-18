package com.innowise.userservice.exception;

import com.innowise.userservice.model.enums.ErrorMessage;

import java.io.Serial;

/**
 * Exception thrown for invalid request parameters or payload.
 */
public class InvalidRequestException extends ApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidRequestException() {
        super(ErrorMessage.INVALID_REQUEST);
    }

    public InvalidRequestException(String detail) {
        super(ErrorMessage.INVALID_REQUEST, detail);
    }
}
