package com.innowise.userservice.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service for recording custom business metrics.
 * Provides methods to track user operations, authentication, and database
 * performance.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {

    private final MeterRegistry meterRegistry;

    /**
     * Increment counter when a user is created.
     */
    public void incrementUserCreated() {
        Counter.builder("user.created")
                .description("Total number of users created")
                .tag("operation", "create")
                .register(meterRegistry)
                .increment();
        log.debug("Incremented user.created metric");
    }

    /**
     * Increment counter when a user is updated.
     */
    public void incrementUserUpdated() {
        Counter.builder("user.updated")
                .description("Total number of users updated")
                .tag("operation", "update")
                .register(meterRegistry)
                .increment();
        log.debug("Incremented user.updated metric");
    }

    /**
     * Increment counter when a user is deleted.
     */
    public void incrementUserDeleted() {
        Counter.builder("user.deleted")
                .description("Total number of users deleted")
                .tag("operation", "delete")
                .register(meterRegistry)
                .increment();
        log.debug("Incremented user.deleted metric");
    }

    /**
     * Increment counter when a user is retrieved.
     */
    public void incrementUserRetrieved() {
        Counter.builder("user.retrieved")
                .description("Total number of users retrieved")
                .tag("operation", "read")
                .register(meterRegistry)
                .increment();
        log.debug("Incremented user.retrieved metric");
    }

    /**
     * Record the time taken for authentication validation.
     */
    public void recordAuthValidationTime(long duration, TimeUnit unit) {
        Timer.builder("auth.validation.time")
                .description("Time taken to validate authentication")
                .tag("component", "security")
                .register(meterRegistry)
                .record(duration, unit);
        log.debug("Recorded auth validation time: {} {}", duration, unit);
    }

    /**
     * Increment counter when card info is created.
     */
    public void incrementCardInfoCreated() {
        Counter.builder("cardinfo.created")
                .description("Total number of card info records created")
                .tag("operation", "create")
                .register(meterRegistry)
                .increment();
        log.debug("Incremented cardinfo.created metric");
    }

    /**
     * Increment counter when card info is updated.
     */
    public void incrementCardInfoUpdated() {
        Counter.builder("cardinfo.updated")
                .description("Total number of card info records updated")
                .tag("operation", "update")
                .register(meterRegistry)
                .increment();
        log.debug("Incremented cardinfo.updated metric");
    }

    /**
     * Record the time taken for database operations.
     */
    public void recordDatabaseQueryTime(String operation, long duration, TimeUnit unit) {
        Timer.builder("database.query.time")
                .tag("operation", operation)
                .tag("component", "database")
                .description("Time taken for database operations")
                .register(meterRegistry)
                .record(duration, unit);
        log.debug("Recorded database query time for {}: {} {}", operation, duration, unit);
    }

    /**
     * Record time for cache operations.
     */
    public void recordCacheOperationTime(String operation, long duration, TimeUnit unit) {
        Timer.builder("cache.operation.time")
                .tag("operation", operation)
                .tag("component", "cache")
                .description("Time taken for cache operations")
                .register(meterRegistry)
                .record(duration, unit);
        log.debug("Recorded cache operation time for {}: {} {}", operation, duration, unit);
    }

    /**
     * Increment counter for failed operations.
     */
    public void incrementOperationFailure(String operation, String reason) {
        Counter.builder("operation.failure")
                .description("Total number of failed operations")
                .tag("operation", operation)
                .tag("reason", reason)
                .register(meterRegistry)
                .increment();
        log.debug("Incremented operation.failure metric for operation: {}, reason: {}", operation, reason);
    }
}
