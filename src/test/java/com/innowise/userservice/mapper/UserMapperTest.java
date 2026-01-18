package com.innowise.userservice.mapper;

import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setup() {
        mapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void toUserDto_shouldMapAllFields() {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setEmail("john@example.com");
        user.setCards(Collections.emptyList());

        UserDto dto = mapper.toUserDto(user);

        assertNotNull(dto);
        assertEquals(user.getId(), dto.id());
        assertEquals(user.getName(), dto.name());
        assertEquals(user.getSurname(), dto.surname());
        assertEquals(user.getBirthDate(), dto.birthDate());
        assertEquals(user.getEmail(), dto.email());
        assertNotNull(dto.cards());
        assertTrue(dto.cards().isEmpty());
    }

    @Test
    void toUser_shouldMapAllFields_ignoringId() {
        UserDto dto = new UserDto(
                null,
                "Alice",
                "Smith",
                LocalDate.of(1995, 5, 5),
                "alice@example.com",
                Collections.emptyList());

        User user = mapper.toUser(dto);

        assertNotNull(user);
        assertNull(user.getId());
        assertEquals(dto.name(), user.getName());
        assertEquals(dto.surname(), user.getSurname());
        assertEquals(dto.birthDate(), user.getBirthDate());
        assertEquals(dto.email(), user.getEmail());
        assertNotNull(user.getCards());
        assertTrue(user.getCards().isEmpty());
    }

    @Test
    void updateUserFromDto_shouldUpdateAllFields_exceptId() {
        UserDto dto = new UserDto(
                99L,
                "Bob",
                "Builder",
                LocalDate.of(2000, 1, 1),
                "bob@example.com",
                Collections.emptyList());

        User user = new User();
        user.setId(1L);
        user.setName("Old");
        user.setSurname("Name");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setEmail("old@example.com");

        mapper.updateUserFromDto(dto, user);

        assertEquals(1L, user.getId());
        assertEquals("Bob", user.getName());
        assertEquals("Builder", user.getSurname());
        assertEquals(LocalDate.of(2000, 1, 1), user.getBirthDate());
        assertEquals("bob@example.com", user.getEmail());
    }
}
