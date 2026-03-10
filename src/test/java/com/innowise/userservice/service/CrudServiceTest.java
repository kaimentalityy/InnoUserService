package com.innowise.userservice.service;

import com.innowise.userservice.model.dto.UserDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CrudServiceTest {

    @Test
    void testCrudServiceInterfaceContract() {
        CrudService<String, String> service = new TestCrudServiceImpl();

        String created = service.create("test");
        assertNotNull(created);
        assertEquals("created-test", created);

        String updated = service.update("id1", "updated");
        assertNotNull(updated);
        assertEquals("updated-updated", updated);

        assertDoesNotThrow(() -> service.delete("id1"));

        String found = service.findById("id1");
        assertNotNull(found);
        assertEquals("found-id1", found);
    }

    @Test
    void testCrudServiceWithUserDto() {
        CrudService<UserDto, String> userService = new TestUserCrudService();

        UserDto newDto = new UserDto(null, "John", "Doe", LocalDate.of(1990, 1, 1), "john@example.com", null);
        UserDto created = userService.create(newDto);

        assertNotNull(created);
        assertNotNull(created.id());
        assertEquals("John", created.name());
        assertEquals("Doe", created.surname());
        assertEquals("john@example.com", created.email());

        UserDto updateDto = new UserDto(created.id(), "Jane", "Smith", LocalDate.of(1990, 1, 1), "jane@example.com",
                null);
        UserDto updated = userService.update(created.id(), updateDto);

        assertEquals(created.id(), updated.id());
        assertEquals("Jane", updated.name());
        assertEquals("Smith", updated.surname());
        assertEquals("jane@example.com", updated.email());

        UserDto found = userService.findById(created.id());
        assertEquals(updated, found);

        assertDoesNotThrow(() -> userService.delete(created.id()));
    }

    @Test
    void testCrudServiceWithNullParameters() {
        CrudService<String, String> service = new TestCrudServiceImpl();

        assertDoesNotThrow(() -> {
            String result = service.create(null);
            assertEquals("created-null", result);
        });

        assertDoesNotThrow(() -> {
            String result = service.update(null, "test");
            assertEquals("updated-test", result);
        });

        assertDoesNotThrow(() -> {
            String result = service.update("id1", null);
            assertEquals("updated-null", result);
        });

        assertDoesNotThrow(() -> service.delete(null));

        assertDoesNotThrow(() -> {
            String result = service.findById(null);
            assertEquals("found-null", result);
        });
    }

    @Test
    void testCrudServiceWithEmptyStrings() {
        CrudService<String, String> service = new TestCrudServiceImpl();

        String created = service.create("");
        assertEquals("created-", created);

        String updated = service.update("", "");
        assertEquals("updated-", updated);

        String found = service.findById("");
        assertEquals("found-", found);

        assertDoesNotThrow(() -> service.delete(""));
    }

    @Test
    void testCrudServiceMethodChaining() {
        CrudService<String, String> service = new TestCrudServiceImpl();

        String created = service.create("original");
        assertEquals("created-original", created);

        String updated = service.update("some-id", "modified");
        assertEquals("updated-modified", updated);

        String found = service.findById("some-id");
        assertEquals("found-some-id", found);

        assertDoesNotThrow(() -> service.delete("some-id"));

        assertNotNull(created);
        assertNotNull(updated);
        assertNotNull(found);
    }

    private static class TestCrudServiceImpl implements CrudService<String, String> {

        @Override
        public String create(String dto) {
            return "created-" + dto;
        }

        @Override
        public String update(String id, String dto) {
            return "updated-" + dto;
        }

        @Override
        public void delete(String id) {
        }

        @Override
        public String findById(String id) {
            return "found-" + id;
        }
    }

    private static class TestUserCrudService implements CrudService<UserDto, String> {
        private UserDto lastDto;

        @Override
        public UserDto create(UserDto dto) {
            String id = UUID.randomUUID().toString();
            lastDto = new UserDto(id, dto.name(), dto.surname(), dto.birthDate(), dto.email(), dto.cards());
            return lastDto;
        }

        @Override
        public UserDto update(String id, UserDto dto) {
            lastDto = new UserDto(id, dto.name(), dto.surname(), dto.birthDate(), dto.email(), dto.cards());
            return lastDto;
        }

        @Override
        public void delete(String id) {
        }

        @Override
        public UserDto findById(String id) {
            return lastDto;
        }
    }
}
