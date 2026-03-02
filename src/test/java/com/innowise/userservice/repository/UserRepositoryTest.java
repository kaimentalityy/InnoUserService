package com.innowise.userservice.repository;

import com.innowise.userservice.BaseIntegrationTest;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.dao.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class UserRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindById() {
        User saved = userRepository.save(createUser("Kiryl", "Savenka", "kiryl.savenka@innowise.com"));
        Optional<User> found = userRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Kiryl");
        assertThat(found.get().getEmail()).isEqualTo("kiryl.savenka@innowise.com");
    }

    @Test
    void testFindByEmail() {
        userRepository.save(createUser("Alexey", "Krivoy", "alexey@krivoy.com"));
        Optional<User> found = userRepository.findByEmail("alexey@krivoy.com");

        assertThat(found).isPresent();
        assertThat(found.get().getSurname()).isEqualTo("Krivoy");
    }

    @Test
    void testFindUsersByIds() {
        User u1 = userRepository.save(createUser("A", "One", "a@a.com"));
        User u2 = userRepository.save(createUser("B", "Two", "b@b.com"));

        List<User> users = userRepository.findUsersByIds(List.of(u1.getId(), u2.getId()));

        assertThat(users).hasSize(2)
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder("a@a.com", "b@b.com");
    }

    @Test
    void testDeleteUser() {
        User user = userRepository.save(createUser("C", "Three", "c@c.com"));
        String id = user.getId();

        userRepository.deleteById(id);

        assertThat(userRepository.findById(id)).isEmpty();
    }

    @Test
    void testFindUserByEmailNative() {
        userRepository.save(createUser("Ivan", "Petrov", "ivan@petrov.com"));
        Optional<User> found = userRepository.findUserByEmailNative("ivan@petrov.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Ivan");
    }

    @Test
    void testFindByEmail_NotFound() {
        Optional<User> notFound = userRepository.findByEmail("unknown@no.com");
        assertThat(notFound).isEmpty();
    }

    @Test
    void testFindUsersByEmptyIds() {
        List<User> users = userRepository.findUsersByIds(List.of());
        assertThat(users).isEmpty();
    }

    private User createUser(String name, String surname, String email) {
        User user = new User();
        user.setId(java.util.UUID.randomUUID().toString());
        user.setName(name);
        user.setSurname(surname);
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setEmail(email);
        return user;
    }
}
