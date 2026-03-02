package com.innowise.userservice.service;

import com.innowise.userservice.BaseIntegrationTest;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.dao.UserRepository;
import com.innowise.userservice.service.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
class UserServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void init() {
        testUser = createTestUser("Default", "User", "default@example.com");
    }

    @Test
    void testCreateUser() {
        UserDto dto = new UserDto(null, "John", "Doe", LocalDate.of(1990, 1, 1), "john@example.com", null);
        UserDto created = userService.create(dto);

        assertThat(created).isNotNull();
        assertThat(created.email()).isEqualTo("john@example.com");
        assertThat(created.id()).isNotNull();
    }

    @Test
    void testFindById_success() {
        UserDto found = userService.findById(testUser.getId());
        assertThat(found.name()).isEqualTo("Default");
    }

    @Test
    void testFindById_notFound() {
        assertThrows(EntityNotFoundException.class, () -> userService.findById("non-existent"));
    }

    @Test
    void testFindByIds_success() {
        User u1 = createTestUser("U1", "Last1", "u1@example.com");
        User u2 = createTestUser("U2", "Last2", "u2@example.com");

        List<UserDto> results = userService.findByIds(List.of(u1.getId(), u2.getId(), testUser.getId()));

        assertThat(results).hasSize(3);
        assertThat(results.stream().map(UserDto::email))
                .contains("u1@example.com", "u2@example.com", "default@example.com");
    }

    @Test
    void testFindByIds_partialMatch() {
        List<UserDto> results = userService.findByIds(List.of("non-existent", testUser.getId()));
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().email()).isEqualTo("default@example.com");
    }

    @Test
    void testFindByEmail_success() {
        UserDto found = userService.findByEmail("default@example.com");
        assertThat(found).isNotNull();
        assertThat(found.email()).isEqualTo("default@example.com");
    }

    @Test
    void testFindByEmail_notFound() {
        assertThrows(EntityNotFoundException.class, () -> userService.findByEmail("notfound@example.com"));
    }

    @Test
    void testSearchUsers_all() {
        createTestUser("Alice", "Wonder", "alice@example.com");
        createTestUser("Bob", "Builder", "bob@example.com");

        Page<UserDto> page = userService.searchUsers(null, null, null, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void testSearchUsers_byName() {
        createTestUser("Kiryl", "Xi", "kiryl@example.com");

        Page<UserDto> page = userService.searchUsers("Kiryl", null, null, PageRequest.of(0, 10));

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().getFirst().name()).isEqualTo("Kiryl");
    }

    @Test
    void testSearchUsers_bySurname() {
        createTestUser("Lina", "Geller", "lina@example.com");

        Page<UserDto> page = userService.searchUsers(null, "Geller", null, PageRequest.of(0, 10));

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().getFirst().surname()).isEqualTo("Geller");
    }

    @Test
    void testSearchUsers_byEmail() {
        createTestUser("Tom", "Jerry", "tom@example.com");

        Page<UserDto> page = userService.searchUsers(null, null, "tom@example.com", PageRequest.of(0, 10));

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).email()).isEqualTo("tom@example.com");
    }

    @Test
    void testUpdateUser_success() {
        UserDto dto = new UserDto(
                testUser.getId(),
                "Updated",
                "Name",
                LocalDate.of(1999, 9, 9),
                "updated@example.com",
                null);

        UserDto updated = userService.update(testUser.getId(), dto);

        assertThat(updated.name()).isEqualTo("Updated");
        assertThat(updated.email()).isEqualTo("updated@example.com");
    }

    @Test
    void testUpdateUser_notFound() {
        UserDto dto = new UserDto("non-existent", "Ghost", "User", LocalDate.now(), "ghost@example.com", null);
        assertThrows(EntityNotFoundException.class, () -> userService.update("non-existent", dto));
    }

    @Test
    void testDeleteUser_success() {
        User user = createTestUser("Delete", "Me", "delete@example.com");
        userService.delete(user.getId());
        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    void testDeleteUser_notFound() {
        assertThrows(EntityNotFoundException.class, () -> userService.delete("non-existent"));
    }

    @Test
    void testDeleteUserWithNullIdThrows() {
        assertThrows(RuntimeException.class, () -> userService.delete(null));
    }

    @Test
    void testCreateAndFetchConsistency() {
        UserDto dto = new UserDto(null, "Sync", "Check", LocalDate.of(1980, 2, 2), "sync@example.com", null);
        UserDto created = userService.create(dto);
        UserDto found = userService.findById(created.id());
        assertThat(found.email()).isEqualTo("sync@example.com");
    }

    @Test
    void testRepositoryStateAfterCreate() {
        long before = userRepository.count();
        userService.create(new UserDto(null, "Count", "Check", LocalDate.now(), "count@example.com", null));
        assertThat(userRepository.count()).isEqualTo(before + 1);
    }

    private User createTestUser(String name, String surname, String email) {
        User user = new User();
        user.setId(java.util.UUID.randomUUID().toString());
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        return userRepository.save(user);
    }
}
