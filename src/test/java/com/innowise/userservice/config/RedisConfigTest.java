package com.innowise.userservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.model.dto.CardInfoDto;
import com.innowise.userservice.model.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RedisConfigTest {

    private RedisConfig redisConfig;
    private RedisConnectionFactory connectionFactory;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        redisConfig = new RedisConfig();
        connectionFactory = Mockito.mock(RedisConnectionFactory.class);
        objectMapper = redisConfig.userDtoObjectMapper();
    }

    @Test
    void testUserDtoObjectMapperConfiguration() {
        assertNotNull(objectMapper);
        assertDoesNotThrow(() -> objectMapper.writeValueAsString(LocalDate.now()));
    }

    @Test
    void testCacheManagerCreation() {
        RedisCacheManager cacheManager = redisConfig.cacheManager(connectionFactory, objectMapper);
        assertNotNull(cacheManager);
    }

    @Test
    void testRedisTemplateConfiguration() {
        RedisTemplate<String, Object> template = redisConfig.redisTemplate(connectionFactory, objectMapper);
        assertNotNull(template.getConnectionFactory());
        assertNotNull(template.getKeySerializer());
        assertNotNull(template.getValueSerializer());
        assertNotNull(template.getHashKeySerializer());
        assertNotNull(template.getHashValueSerializer());
    }

    @Test
    void testRedisTemplateSerializationRoundTripUserDto() throws Exception {
        ObjectMapper mapper = redisConfig.userDtoObjectMapper();
        UserDto user = new UserDto("user-1", "John", "Doe", LocalDate.of(1990, 1, 1), "john@example.com", null);

        byte[] bytes = mapper.writeValueAsBytes(user);
        UserDto result = mapper.readValue(bytes, UserDto.class);

        assertEquals(user.id(), result.id());
        assertEquals(user.name(), result.name());
        assertEquals(user.email(), result.email());
    }

    @Test
    void testRedisTemplateSerializationRoundTripCardInfoDto() throws Exception {
        ObjectMapper mapper = redisConfig.userDtoObjectMapper();
        CardInfoDto card = new CardInfoDto(1L, "user-1", "1234567890123456", "John Doe", LocalDate.of(2030, 12, 31));

        byte[] bytes = mapper.writeValueAsBytes(card);
        CardInfoDto result = mapper.readValue(bytes, CardInfoDto.class);

        assertEquals(card.id(), result.id());
        assertEquals(card.number(), result.number());
        assertEquals(card.holder(), result.holder());
    }

    @Test
    void testRedisConfigConstructorCoverage() {
        RedisConfig config = new RedisConfig();
        assertNotNull(config);
    }
}
