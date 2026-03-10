package com.innowise.userservice.health;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RedisHealthIndicator.
 */
@ExtendWith(MockitoExtension.class)
class RedisHealthIndicatorTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedisConnectionFactory connectionFactory;

    @Mock
    private RedisConnection connection;

    @InjectMocks
    private RedisHealthIndicator healthIndicator;

    @Test
    void health_WhenRedisIsHealthy_ShouldReturnUp() {
        when(redisTemplate.getConnectionFactory()).thenReturn(connectionFactory);
        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.ping()).thenReturn("PONG");

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("Redis", health.getDetails().get("cache"));
        assertEquals("reachable", health.getDetails().get("status"));
        assertEquals("PONG", health.getDetails().get("response"));
    }

    @Test
    void health_WhenRedisIsDown_ShouldReturnDown() {
        when(redisTemplate.getConnectionFactory()).thenReturn(connectionFactory);
        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.ping()).thenThrow(new RuntimeException("Redis connection failed"));

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Redis", health.getDetails().get("cache"));
        assertEquals("error", health.getDetails().get("status"));
        assertTrue(health.getDetails().containsKey("error"));
    }

    @Test
    void health_WhenRedisReturnsNotPong_ShouldReturnDown() {
        when(redisTemplate.getConnectionFactory()).thenReturn(connectionFactory);
        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connection.ping()).thenReturn("ERROR");

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Redis", health.getDetails().get("cache"));
        assertEquals("unreachable", health.getDetails().get("status"));
        assertEquals("ERROR", health.getDetails().get("response"));
    }
}
