package com.innowise.userservice.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration for custom business metrics using Micrometer.
 * These metrics will be exposed via Prometheus and visualized in Grafana.
 */
@Configuration
@EnableAspectJAutoProxy
public class MetricsConfig {

    /**
     * Customizes the MeterRegistry to add common tags to all metrics.
     * These tags help identify metrics in Prometheus/Grafana.
     */
    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
                .commonTags(
                        "application", "inno-user-service",
                        "service", "user-service");
    }

    /**
     * Enables @Timed annotation support for method execution timing.
     * This allows precise timing of specific methods.
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    /**
     * Counter for tracking total users created
     */
    @Bean
    public Counter usersCreatedCounter(MeterRegistry registry) {
        return Counter.builder("users.created.total")
                .description("Total number of users created")
                .tag("service", "user-service")
                .register(registry);
    }

    /**
     * Counter for tracking users updated
     */
    @Bean
    public Counter usersUpdatedCounter(MeterRegistry registry) {
        return Counter.builder("users.updated.total")
                .description("Total number of users updated")
                .tag("service", "user-service")
                .register(registry);
    }

    /**
     * Counter for tracking users deleted
     */
    @Bean
    public Counter usersDeletedCounter(MeterRegistry registry) {
        return Counter.builder("users.deleted.total")
                .description("Total number of users deleted")
                .tag("service", "user-service")
                .register(registry);
    }

    /**
     * Timer for tracking user operation duration
     */
    @Bean
    public Timer userOperationTimer(MeterRegistry registry) {
        return Timer.builder("user.operation.duration")
                .description("Time taken for user operations")
                .tag("service", "user-service")
                .register(registry);
    }

    /**
     * Counter for tracking Redis cache hits
     */
    @Bean
    public Counter redisCacheHitsCounter(MeterRegistry registry) {
        return Counter.builder("redis.cache.hits")
                .description("Number of Redis cache hits")
                .tag("service", "user-service")
                .register(registry);
    }

    /**
     * Counter for tracking Redis cache misses
     */
    @Bean
    public Counter redisCacheMissesCounter(MeterRegistry registry) {
        return Counter.builder("redis.cache.misses")
                .description("Number of Redis cache misses")
                .tag("service", "user-service")
                .register(registry);
    }

    /**
     * Counter for tracking Auth Service HTTP calls
     */
    @Bean
    public Counter authServiceCallsCounter(MeterRegistry registry) {
        return Counter.builder("auth.service.calls.total")
                .description("Total number of HTTP calls to Auth Service")
                .tag("service", "user-service")
                .register(registry);
    }

    /**
     * Counter for tracking Order Service HTTP calls
     */
    @Bean
    public Counter orderServiceCallsCounter(MeterRegistry registry) {
        return Counter.builder("order.service.calls.total")
                .description("Total number of HTTP calls to Order Service")
                .tag("service", "user-service")
                .register(registry);
    }

    /**
     * Timer for tracking database query duration
     */
    @Bean
    public Timer databaseQueryTimer(MeterRegistry registry) {
        return Timer.builder("database.query.duration")
                .description("Time taken for database queries")
                .tag("service", "user-service")
                .register(registry);
    }
}
