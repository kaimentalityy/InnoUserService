package com.innowise.userservice.model.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorDtoTest {

    @Test
    void testBoilerplate() {
        ErrorDto error1 = new ErrorDto("msg", 400);
        ErrorDto error2 = new ErrorDto("msg", 400);
        ErrorDto error3 = new ErrorDto("other", 500);

        assertEquals(error1, error2);
        assertNotEquals(error1, error3);
        assertEquals(error1.hashCode(), error2.hashCode());
        assertNotNull(error1.toString());

        ErrorDto empty = new ErrorDto();
        empty.setMessage("m");
        empty.setStatus(200);
        assertEquals("m", empty.getMessage());
        assertEquals(200, empty.getStatus());
    }
}
