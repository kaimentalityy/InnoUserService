package com.innowise.userservice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Utility class for structured logging with MDC (Mapped Diagnostic Context).
 * Helps add contextual information to log messages.
 */
public class LoggingUtil {

    /**
     * Log with context information.
     * The trace context is automatically added by the tracing framework.
     */
    public static void logWithContext(Class<?> clazz, String message, Object... args) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info(message, args);
    }

    /**
     * Add custom key-value pair to MDC for contextual logging.
     */
    public static void addToContext(String key, String value) {
        if (value != null) {
            MDC.put(key, value);
        }
    }

    /**
     * Remove a specific key from MDC.
     */
    public static void removeFromContext(String key) {
        MDC.remove(key);
    }

    /**
     * Clear all MDC context.
     * Should be called when request processing is complete.
     */
    public static void clearContext() {
        MDC.clear();
    }

    /**
     * Add user context to logs.
     */
    public static void addUserContext(String userId, String username) {
        addToContext("userId", userId);
        addToContext("username", username);
    }

    /**
     * Add request context to logs.
     */
    public static void addRequestContext(String requestId, String endpoint) {
        addToContext("requestId", requestId);
        addToContext("endpoint", endpoint);
    }
}
