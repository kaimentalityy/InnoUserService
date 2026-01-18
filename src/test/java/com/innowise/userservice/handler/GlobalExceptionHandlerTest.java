package com.innowise.userservice.handler;

import com.innowise.userservice.exception.*;
import com.innowise.userservice.model.dto.ErrorDto;
import com.innowise.userservice.model.enums.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleBadRequestException() {
        InvalidRequestException ex = new InvalidRequestException("invalid input");
        ErrorDto dto = handler.handleApplicationException(ex);

        assertNotNull(dto);
        assertTrue(dto.getMessage().contains("Invalid request") || dto.getMessage().contains("invalid input"));
        assertEquals(400, dto.getStatus());
    }

    @Test
    void testHandleConflictException() {
        EntityAlreadyExistsException ex = new EntityAlreadyExistsException("User", "email", "test@example.com");
        ErrorDto dto = handler.handleApplicationException(ex);

        assertNotNull(dto);
        assertTrue(dto.getMessage().contains("Entity already exists") || dto.getMessage().contains("already exists"));
        assertEquals(409, dto.getStatus());
    }

    @Test
    void testHandleNotFoundException() {
        EntityNotFoundException ex = new EntityNotFoundException("User", "id", "123");
        ErrorDto dto = handler.handleApplicationException(ex);

        assertNotNull(dto);
        assertTrue(dto.getMessage().contains("not found"));
        assertEquals(404, dto.getStatus());
    }

    @Test
    void testHandleValidationErrors() throws NoSuchMethodException {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "objectName");
        bindingResult.addError(new FieldError("objectName", "field1", "must not be null"));
        bindingResult.addError(new FieldError("objectName", "field2", "must be positive"));

        Method method = getClass().getDeclaredMethod("testHandleValidationErrors");
        MethodParameter methodParameter = new MethodParameter(method, -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ErrorDto dto = handler.handleValidationErrors(ex);

        assertNotNull(dto);
        assertTrue(dto.getMessage().contains("field1"));
        assertTrue(dto.getMessage().contains("field2"));
        assertEquals(400, dto.getStatus());
    }

    @Test
    void testHandleUnexpectedException() {
        RuntimeException ex = new RuntimeException("unexpected error");
        ErrorDto dto = handler.handleUnexpected(ex);

        assertNotNull(dto);
        assertEquals(500, dto.getStatus());
        assertTrue(dto.getMessage().toLowerCase().contains("internal")
                || dto.getMessage().toLowerCase().contains("unexpected"));
    }
}
