package com.innowise.userservice.exception;

import com.innowise.userservice.model.enums.ErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExceptionTests {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }


    @Test
    void accessDeniedCustomException_shouldHaveCorrectMessage() {
        AccessDeniedCustomException ex = new AccessDeniedCustomException();
        assertEquals(ErrorMessage.ACCESS_DENIED, ex.getErrorMessage());
    }

    @Test
    void customConstraintViolationException_shouldStoreDetail() {
        String msg = "Violation occurred";
        CustomConstraintViolationException ex = new CustomConstraintViolationException(msg);
        assertEquals(ErrorMessage.CONSTRAINT_VIOLATION, ex.getErrorMessage());
        assertEquals(msg, ex.getMessage());
    }

    @Test
    void entityAlreadyExistsException_defaultConstructor() {
        EntityAlreadyExistsException ex = new EntityAlreadyExistsException();
        assertEquals(ErrorMessage.ENTITY_ALREADY_EXISTS, ex.getErrorMessage());
    }

    @Test
    void entityAlreadyExistsException_customConstructor() {
        EntityAlreadyExistsException ex = new EntityAlreadyExistsException("User", "email", "a@b.com");
        assertTrue(ex.getMessage().contains("User with email 'a@b.com' already exists"));
    }

    @Test
    void entityNotFoundException_defaultConstructor() {
        EntityNotFoundException ex = new EntityNotFoundException();
        assertEquals(ErrorMessage.ENTITY_NOT_FOUND, ex.getErrorMessage());
    }

    @Test
    void entityNotFoundException_customConstructor() {
        EntityNotFoundException ex = new EntityNotFoundException("Card", "number", "1234");
        assertTrue(ex.getMessage().contains("Card with number '1234' not found"));
    }

    @Test
    void invalidRequestException_defaultConstructor() {
        InvalidRequestException ex = new InvalidRequestException();
        assertEquals(ErrorMessage.INVALID_REQUEST, ex.getErrorMessage());
    }

    @Test
    void invalidRequestException_withDetail() {
        String detail = "Missing field";
        InvalidRequestException ex = new InvalidRequestException(detail);
        assertEquals(ErrorMessage.INVALID_REQUEST, ex.getErrorMessage());
        assertEquals(detail, ex.getMessage());
    }
}