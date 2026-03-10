package com.innowise.userservice.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MetricsServiceTest {

    private MeterRegistry meterRegistry;
    private MetricsService metricsService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        metricsService = new MetricsService(meterRegistry);
    }

    @Test
    void testIncrementUserCreated() {
        metricsService.incrementUserCreated();
        assertEquals(1.0, meterRegistry.get("user.created").tag("operation", "create").counter().count());
    }

    @Test
    void testIncrementUserUpdated() {
        metricsService.incrementUserUpdated();
        assertEquals(1.0, meterRegistry.get("user.updated").tag("operation", "update").counter().count());
    }

    @Test
    void testIncrementUserDeleted() {
        metricsService.incrementUserDeleted();
        assertEquals(1.0, meterRegistry.get("user.deleted").tag("operation", "delete").counter().count());
    }

    @Test
    void testIncrementUserRetrieved() {
        metricsService.incrementUserRetrieved();
        assertEquals(1.0, meterRegistry.get("user.retrieved").tag("operation", "read").counter().count());
    }

    @Test
    void testRecordAuthValidationTime() {
        long duration = 100L;
        TimeUnit unit = TimeUnit.MILLISECONDS;

        metricsService.recordAuthValidationTime(duration, unit);

        Timer timer = meterRegistry.get("auth.validation.time").tag("component", "security").timer();
        assertEquals(1, timer.count());
        assertEquals(100.0, timer.totalTime(TimeUnit.MILLISECONDS));
    }

    @Test
    void testIncrementCardInfoCreated() {
        metricsService.incrementCardInfoCreated();
        assertEquals(1.0, meterRegistry.get("cardinfo.created").tag("operation", "create").counter().count());
    }

    @Test
    void testIncrementCardInfoUpdated() {
        metricsService.incrementCardInfoUpdated();
        assertEquals(1.0, meterRegistry.get("cardinfo.updated").tag("operation", "update").counter().count());
    }

    @Test
    void testRecordDatabaseQueryTime() {
        String operation = "SELECT";
        long duration = 50L;
        TimeUnit unit = TimeUnit.MILLISECONDS;

        metricsService.recordDatabaseQueryTime(operation, duration, unit);

        Timer timer = meterRegistry.get("database.query.time").tag("operation", operation).tag("component", "database")
                .timer();
        assertEquals(1, timer.count());
        assertEquals(50.0, timer.totalTime(TimeUnit.MILLISECONDS));
    }

    @Test
    void testRecordCacheOperationTime() {
        String operation = "GET";
        long duration = 25L;
        TimeUnit unit = TimeUnit.MICROSECONDS;

        metricsService.recordCacheOperationTime(operation, duration, unit);

        Timer timer = meterRegistry.get("cache.operation.time").tag("operation", operation).tag("component", "cache")
                .timer();
        assertEquals(1, timer.count());
        assertEquals(25.0, timer.totalTime(TimeUnit.MICROSECONDS));
    }

    @Test
    void testIncrementOperationFailure() {
        String operation = "CREATE";
        String reason = "VALIDATION_ERROR";

        metricsService.incrementOperationFailure(operation, reason);

        assertEquals(1.0, meterRegistry.get("operation.failure")
                .tag("operation", operation)
                .tag("reason", reason)
                .counter().count());
    }

    @Test
    void testIncrementOperationFailureWithNullOperation() {
        String reason = "SYSTEM_ERROR";

        metricsService.incrementOperationFailure(null, reason);

        assertEquals(1.0, meterRegistry.get("operation.failure")
                .tag("operation", "unknown")
                .tag("reason", reason)
                .counter().count());
    }

    @Test
    void testIncrementOperationFailureWithNullReason() {
        String operation = "UPDATE";

        metricsService.incrementOperationFailure(operation, null);

        assertEquals(1.0, meterRegistry.get("operation.failure")
                .tag("operation", operation)
                .tag("reason", "unknown")
                .counter().count());
    }

    @Test
    void testRecordDatabaseQueryTimeWithDifferentTimeUnits() {
        metricsService.recordDatabaseQueryTime("INSERT", 1000L, TimeUnit.NANOSECONDS);
        assertEquals(1, meterRegistry.get("database.query.time").tag("operation", "INSERT").timer().count());

        metricsService.recordDatabaseQueryTime("DELETE", 1L, TimeUnit.SECONDS);
        assertEquals(1, meterRegistry.get("database.query.time").tag("operation", "DELETE").timer().count());
    }

    @Test
    void testRecordCacheOperationTimeWithDifferentOperations() {
        metricsService.recordCacheOperationTime("PUT", 10L, TimeUnit.MILLISECONDS);
        assertEquals(1, meterRegistry.get("cache.operation.time").tag("operation", "PUT").timer().count());

        metricsService.recordCacheOperationTime("DELETE", 5L, TimeUnit.MILLISECONDS);
        assertEquals(1, meterRegistry.get("cache.operation.time").tag("operation", "DELETE").timer().count());
    }

    @Test
    void testRecordAuthValidationTimeWithZeroDuration() {
        metricsService.recordAuthValidationTime(0L, TimeUnit.MILLISECONDS);
        assertEquals(1, meterRegistry.get("auth.validation.time").timer().count());
    }

    @Test
    void testRecordAuthValidationTimeWithNegativeDuration() {
        metricsService.recordAuthValidationTime(-10L, TimeUnit.MILLISECONDS);
        assertEquals(0, meterRegistry.get("auth.validation.time").timer().count());
    }

    @Test
    void testMultipleMetricCalls() {
        metricsService.incrementUserCreated();
        metricsService.incrementUserUpdated();
        metricsService.incrementUserDeleted();
        metricsService.incrementUserRetrieved();

        assertEquals(1.0, meterRegistry.get("user.created").counter().count());
        assertEquals(1.0, meterRegistry.get("user.updated").counter().count());
        assertEquals(1.0, meterRegistry.get("user.deleted").counter().count());
        assertEquals(1.0, meterRegistry.get("user.retrieved").counter().count());
    }

    @Test
    void testCardInfoMetricsMultipleCalls() {
        metricsService.incrementCardInfoCreated();
        metricsService.incrementCardInfoUpdated();

        assertEquals(1.0, meterRegistry.get("cardinfo.created").counter().count());
        assertEquals(1.0, meterRegistry.get("cardinfo.updated").counter().count());
    }
}
