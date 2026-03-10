package com.innowise.userservice.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

class LoggingUtilTest {

    @BeforeEach
    void setUp() {
        
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        
        MDC.clear();
    }

    @Test
    void testLogWithContext() {
        
        assertDoesNotThrow(() -> {
            LoggingUtil.logWithContext(LoggingUtilTest.class, "Test message with args: {}", "arg1");
        });
        
        assertDoesNotThrow(() -> {
            LoggingUtil.logWithContext(String.class, "Simple test message");
        });
        
        assertDoesNotThrow(() -> {
            LoggingUtil.logWithContext(Integer.class, "Number: {}", 42);
        });
    }

    @Test
    void testLogWithContextWithNullClass() {
        
        assertThrows(NullPointerException.class, () -> {
            LoggingUtil.logWithContext(null, "Test message");
        });
    }

    @Test
    void testLogWithContextWithNullMessage() {
        assertDoesNotThrow(() -> {
            LoggingUtil.logWithContext(LoggingUtilTest.class, null);
        });
    }

    @Test
    void testLogWithContextWithNullArgs() {
        assertDoesNotThrow(() -> {
            LoggingUtil.logWithContext(LoggingUtilTest.class, "Test message", null, null, null);
        });
    }

    @Test
    void testLogWithContextWithMixedArgs() {
        assertDoesNotThrow(() -> {
            LoggingUtil.logWithContext(LoggingUtilTest.class, "Mixed args: {}, {}, {}", "string", 123, true);
        });
    }

    @Test
    void testAddToContext() {
        
        assertDoesNotThrow(() -> {
            LoggingUtil.addToContext("testKey", "testValue");
        });
        
        assertDoesNotThrow(() -> {
            LoggingUtil.addToContext("userId", "user123");
        });
        
        assertDoesNotThrow(() -> {
            LoggingUtil.addToContext("requestId", "req-456");
        });
    }

    @Test
    void testAddToContextWithNullKey() {
        
        assertThrows(IllegalArgumentException.class, () -> {
            LoggingUtil.addToContext(null, "value");
        });
    }

    @Test
    void testAddToContextWithNullValue() {
        
        assertDoesNotThrow(() -> {
            LoggingUtil.addToContext("testKey", null);
        });
        
        
        assertNull(MDC.get("testKey"));
    }

    @Test
    void testAddToContextWithEmptyString() {
        assertDoesNotThrow(() -> {
            LoggingUtil.addToContext("testKey", "");
        });
        
        
        assertEquals("", MDC.get("testKey"));
    }

    @Test
    void testAddToContextWithSpecialCharacters() {
        assertDoesNotThrow(() -> {
            LoggingUtil.addToContext("special.key", "special-value-with-dashes_and_underscores");
        });
        
        assertEquals("special-value-with-dashes_and_underscores", MDC.get("special.key"));
    }

    @Test
    void testAddToContextWithUnicode() {
        assertDoesNotThrow(() -> {
            LoggingUtil.addToContext("unicodeKey", "текст-на-русском");
        });
        
        assertEquals("текст-на-русском", MDC.get("unicodeKey"));
    }

    @Test
    void testRemoveFromContext() {
        
        LoggingUtil.addToContext("testKey", "testValue");
        assertEquals("testValue", MDC.get("testKey"));
        
        
        assertDoesNotThrow(() -> {
            LoggingUtil.removeFromContext("testKey");
        });
        
        
        assertNull(MDC.get("testKey"));
    }

    @Test
    void testRemoveFromContextWithNonExistentKey() {
        assertDoesNotThrow(() -> {
            LoggingUtil.removeFromContext("nonExistentKey");
        });
    }

    @Test
    void testRemoveFromContextWithNullKey() {
        
        assertThrows(IllegalArgumentException.class, () -> {
            LoggingUtil.removeFromContext(null);
        });
    }

    @Test
    void testClearContext() {
        
        LoggingUtil.addToContext("key1", "value1");
        LoggingUtil.addToContext("key2", "value2");
        LoggingUtil.addToContext("key3", "value3");
        
        
        assertEquals("value1", MDC.get("key1"));
        assertEquals("value2", MDC.get("key2"));
        assertEquals("value3", MDC.get("key3"));
        
        
        assertDoesNotThrow(() -> {
            LoggingUtil.clearContext();
        });
        
        
        assertNull(MDC.get("key1"));
        assertNull(MDC.get("key2"));
        assertNull(MDC.get("key3"));
    }

    @Test
    void testClearContextWhenEmpty() {
        assertDoesNotThrow(() -> {
            LoggingUtil.clearContext();
        });
    }

    @Test
    void testAddUserContext() {
        assertDoesNotThrow(() -> {
            LoggingUtil.addUserContext("user123", "john.doe");
        });
        
        assertEquals("user123", MDC.get("userId"));
        assertEquals("john.doe", MDC.get("username"));
    }

    @Test
    void testAddUserContextWithNullValues() {
        assertDoesNotThrow(() -> {
            LoggingUtil.addUserContext(null, null);
        });
        
        
        assertNull(MDC.get("userId"));
        assertNull(MDC.get("username"));
    }

    @Test
    void testAddUserContextWithPartialNullValues() {
        assertDoesNotThrow(() -> {
            LoggingUtil.addUserContext("user123", null);
        });
        
        assertEquals("user123", MDC.get("userId"));
        assertNull(MDC.get("username"));
        
        
        MDC.clear();
        
        assertDoesNotThrow(() -> {
            LoggingUtil.addUserContext(null, "john.doe");
        });
        
        assertNull(MDC.get("userId"));
        assertEquals("john.doe", MDC.get("username"));
    }

    @Test
    void testAddUserContextWithEmptyStrings() {
        assertDoesNotThrow(() -> {
            LoggingUtil.addUserContext("", "");
        });
        
        assertEquals("", MDC.get("userId"));
        assertEquals("", MDC.get("username"));
    }

    @Test
    void testAddRequestContext() {
        assertDoesNotThrow(() -> {
            LoggingUtil.addRequestContext("req-123", "/api/users");
        });
        
        assertEquals("req-123", MDC.get("requestId"));
        assertEquals("/api/users", MDC.get("endpoint"));
    }

    @Test
    void testAddRequestContextWithNullValues() {
        assertDoesNotThrow(() -> {
            LoggingUtil.addRequestContext(null, null);
        });
        
        
        assertNull(MDC.get("requestId"));
        assertNull(MDC.get("endpoint"));
    }

    @Test
    void testAddRequestContextWithPartialNullValues() {
        assertDoesNotThrow(() -> {
            LoggingUtil.addRequestContext("req-123", null);
        });
        
        assertEquals("req-123", MDC.get("requestId"));
        assertNull(MDC.get("endpoint"));
        
        
        MDC.clear();
        
        assertDoesNotThrow(() -> {
            LoggingUtil.addRequestContext(null, "/api/users");
        });
        
        assertNull(MDC.get("requestId"));
        assertEquals("/api/users", MDC.get("endpoint"));
    }

    @Test
    void testAddRequestContextWithEmptyStrings() {
        assertDoesNotThrow(() -> {
            LoggingUtil.addRequestContext("", "");
        });
        
        assertEquals("", MDC.get("requestId"));
        assertEquals("", MDC.get("endpoint"));
    }

    @Test
    void testMultipleOperations() {
        
        LoggingUtil.addUserContext("user123", "john.doe");
        LoggingUtil.addRequestContext("req-456", "/api/cards");
        
        assertEquals("user123", MDC.get("userId"));
        assertEquals("john.doe", MDC.get("username"));
        assertEquals("req-456", MDC.get("requestId"));
        assertEquals("/api/cards", MDC.get("endpoint"));
        
        
        LoggingUtil.removeFromContext("username");
        
        assertEquals("user123", MDC.get("userId"));
        assertNull(MDC.get("username"));
        assertEquals("req-456", MDC.get("requestId"));
        assertEquals("/api/cards", MDC.get("endpoint"));
        
        
        LoggingUtil.clearContext();
        
        assertNull(MDC.get("userId"));
        assertNull(MDC.get("username"));
        assertNull(MDC.get("requestId"));
        assertNull(MDC.get("endpoint"));
    }
}
