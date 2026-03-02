package com.innowise.userservice.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Custom health indicator for Redis connectivity.
 * Checks if Redis cache is available and responsive.
 * Only active when spring.data.redis.enabled=true
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.data.redis.enabled", havingValue = "true", matchIfMissing = true)
public class RedisHealthIndicator implements HealthIndicator {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Health health() {
        try {
            String pong = redisTemplate.getConnectionFactory()
                    .getConnection()
                    .ping();

            if ("PONG".equals(pong)) {
                return Health.up()
                        .withDetail("cache", "Redis")
                        .withDetail("status", "reachable")
                        .withDetail("response", pong)
                        .build();
            } else {
                return Health.down()
                        .withDetail("cache", "Redis")
                        .withDetail("status", "unreachable")
                        .withDetail("response", pong)
                        .build();
            }
        } catch (Exception e) {
            log.error("Redis health check failed", e);
            return Health.down()
                    .withDetail("cache", "Redis")
                    .withDetail("status", "error")
                    .withDetail("error", e.getMessage())
                    .withException(e)
                    .build();
        }
    }
}
