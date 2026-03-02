package com.innowise.userservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.innowise.userservice.model.dto.CardInfoDto;
import com.innowise.userservice.model.dto.UserDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for Redis caching setup.
 * Enables and configures Redis as a caching mechanism for the application.
 * Provides custom serialization configuration for Redis cache to handle Java 8
 * time types.
 * Only active when spring.data.redis.enabled=true
 *
 * @author Your Name
 * @version 1.0
 * @since 2024
 *
 * @see org.springframework.cache.annotation.EnableCaching
 * @see org.springframework.data.redis.cache.RedisCacheConfiguration
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.data.redis.enabled", havingValue = "true", matchIfMissing = true)
public class RedisConfig {

    /**
     * Configures Redis cache with custom serialization settings.
     * Creates a RedisCacheConfiguration that uses Jackson JSON serialization
     * with support for Java 8 time types (LocalDate, LocalDateTime, etc.).
     * This ensures proper serialization/deserialization of temporal objects
     * when storing and retrieving from Redis cache.
     *
     * @return RedisCacheConfiguration configured with JSON serialization
     *         and Java 8 time module support
     *
     * @see ObjectMapper
     * @see JavaTimeModule
     * @see GenericJackson2JsonRedisSerializer
     */
    @Bean
    public ObjectMapper userDtoObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper mapper) {
        Jackson2JsonRedisSerializer<UserDto> userSerializer = new Jackson2JsonRedisSerializer<>(mapper, UserDto.class);

        Jackson2JsonRedisSerializer<CardInfoDto> cardSerializer = new Jackson2JsonRedisSerializer<>(mapper, CardInfoDto.class);

        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put("users", RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(userSerializer)));
        cacheConfigs.put("cards", RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(cardSerializer)));

        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory,
            ObjectMapper userDtoObjectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(userDtoObjectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }

}
