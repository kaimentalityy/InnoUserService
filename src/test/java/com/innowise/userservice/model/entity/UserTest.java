package com.innowise.userservice.model.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void testUserFieldsAndAccessors() {
        User user = new User();
        user.setId("user-1");
        user.setName("John");
        user.setSurname("Doe");
        user.setBirthDate(LocalDate.of(1990, 5, 20));
        user.setEmail("john.doe@example.com");

        assertThat(user.getId()).isEqualTo("user-1");
        assertThat(user.getName()).isEqualTo("John");
        assertThat(user.getSurname()).isEqualTo("Doe");
        assertThat(user.getBirthDate()).isEqualTo(LocalDate.of(1990, 5, 20));
        assertThat(user.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void testCardsListOperations() {
        User user = new User();
        assertThat(user.getCards()).isEmpty();

        CardInfo card = new CardInfo();
        user.getCards().add(card);
        assertThat(user.getCards()).containsExactly(card);

        user.getCards().remove(card);
        assertThat(user.getCards()).isEmpty();
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User();
        user1.setId("user-1");
        user1.setEmail("email@example.com");

        User user2 = new User();
        user2.setId("user-1");
        user2.setEmail("email@example.com");

        User user3 = new User();
        user3.setId("user-2");

        assertThat(user1).isEqualTo(user2);
        assertThat(user1).isNotEqualTo(user3);
        assertThat(user1).isNotEqualTo(null);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
        assertThat(user1.hashCode()).isNotEqualTo(user3.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        User user = new User();
        user.setId("user-1");
        user.setName("John");
        user.setSurname("Doe");
        user.setBirthDate(LocalDate.of(1990, 5, 20));
        user.setEmail("john.doe@example.com");

        String str = user.toString();
        assertThat(str).contains("1", "John", "Doe", "1990-05-20", "john.doe@example.com");
    }
}
