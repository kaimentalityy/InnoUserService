package com.innowise.userservice.exception;

import java.io.Serial;
import java.io.Serializable;

import com.innowise.userservice.model.enums.ErrorMessage;
import lombok.Getter;

/**
 * Base class for all custom application exceptions.
 * Stores a reference to a standardized {@link ErrorMessage}.
 */
@Getter
public class ApplicationException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ErrorMessage errorMessage;

    public ApplicationException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

    public ApplicationException(ErrorMessage errorMessage, String detail) {
        super(detail);
        this.errorMessage = errorMessage;
    }
}
