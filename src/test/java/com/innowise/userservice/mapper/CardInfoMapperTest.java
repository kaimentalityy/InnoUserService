package com.innowise.userservice.mapper;

import com.innowise.userservice.model.dto.CardInfoDto;
import com.innowise.userservice.model.entity.CardInfo;
import com.innowise.userservice.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CardInfoMapperTest {

    private CardInfoMapper mapper;

    @BeforeEach
    void setup() {
        mapper = Mappers.getMapper(CardInfoMapper.class);
    }

    @Test
    void toEntity_shouldMapDtoToEntityIgnoringIdAndUser() {
        CardInfoDto dto = new CardInfoDto(
                1L,
                "user-5",
                "1234567890123",
                "John Doe",
                LocalDate.of(2030, 12, 31));

        CardInfo entity = mapper.toEntity(dto);

        assertNull(entity.getId());
        assertNull(entity.getUser());
        assertEquals(dto.number(), entity.getNumber());
        assertEquals(dto.holder(), entity.getHolder());
        assertEquals(dto.expirationDate(), entity.getExpirationDate());
    }

    @Test
    void toDto_shouldMapEntityToDtoIncludingUserId() {
        User user = new User();
        user.setId("user-10");

        CardInfo entity = new CardInfo();
        entity.setId(1L);
        entity.setNumber("1234567890123");
        entity.setHolder("John Doe");
        entity.setExpirationDate(LocalDate.of(2030, 12, 31));
        entity.setUser(user);

        CardInfoDto dto = mapper.toDto(entity);

        assertEquals(entity.getNumber(), dto.number());
        assertEquals(entity.getHolder(), dto.holder());
        assertEquals(entity.getExpirationDate(), dto.expirationDate());
        assertEquals(user.getId(), dto.userId());
    }

    @Test
    void updateEntity_shouldUpdateFieldsWithoutChangingUser() {
        User user = new User();
        user.setId("user-10");

        CardInfo entity = new CardInfo();
        entity.setNumber("1111111111111");
        entity.setHolder("Old Name");
        entity.setExpirationDate(LocalDate.of(2025, 1, 1));
        entity.setUser(user);

        CardInfoDto dto = new CardInfoDto(
                1L,
                "user-5",
                "2222222222222",
                "New Name",
                LocalDate.of(2030, 12, 31));

        mapper.updateEntity(dto, entity);

        assertEquals(dto.number(), entity.getNumber());
        assertEquals(dto.holder(), entity.getHolder());
        assertEquals(dto.expirationDate(), entity.getExpirationDate());
        assertEquals(user, entity.getUser());
    }

}
