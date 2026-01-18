package com.innowise.userservice.exception;

import com.innowise.userservice.model.enums.ErrorMessage;

import java.io.Serial;

/**
 * Exception thrown when an entity is not found.
 */
public class EntityNotFoundException extends ApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public EntityNotFoundException() {
        super(ErrorMessage.ENTITY_NOT_FOUND);
    }

    public EntityNotFoundException(String entity, String field, Object value) {
        super(ErrorMessage.ENTITY_NOT_FOUND,
                String.format("%s with %s '%s' not found", entity, field, value));
    }
}
