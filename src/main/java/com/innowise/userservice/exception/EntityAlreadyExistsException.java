package com.innowise.userservice.exception;

import com.innowise.userservice.model.enums.ErrorMessage;

import java.io.Serial;

/**
 * Exception thrown when an entity already exists.
 */
public class EntityAlreadyExistsException extends ApplicationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public EntityAlreadyExistsException() {
        super(ErrorMessage.ENTITY_ALREADY_EXISTS);
    }

    public EntityAlreadyExistsException(String entity, String field, Object value) {
        super(ErrorMessage.ENTITY_ALREADY_EXISTS,
                String.format("%s with %s '%s' already exists", entity, field, value));
    }
}
