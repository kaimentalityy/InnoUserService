package com.innowise.userservice.model.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CardInfoTest {

    @Test
    void testCardInfoFieldsAndAccessors() {
        User user = new User();
        user.setId("user-1");

        CardInfo card = new CardInfo();
        card.setId(100L);
        card.setUser(user);
        card.setNumber("1234567890123456");
        card.setHolder("John Doe");
        card.setExpirationDate(LocalDate.of(2030, 12, 31));

        assertThat(card.getId()).isEqualTo(100L);
        assertThat(card.getUser()).isEqualTo(user);
        assertThat(card.getNumber()).isEqualTo("1234567890123456");
        assertThat(card.getHolder()).isEqualTo("John Doe");
        assertThat(card.getExpirationDate()).isEqualTo(LocalDate.of(2030, 12, 31));
    }

    @Test
    void testEqualsAndHashCode() {
        User user = new User();
        user.setId("user-1");

        CardInfo card1 = new CardInfo();
        card1.setId(1L);
        card1.setUser(user);

        CardInfo card2 = new CardInfo();
        card2.setId(1L);
        card2.setUser(user);

        CardInfo card3 = new CardInfo();
        card3.setId(2L);
        card3.setUser(user);

        assertThat(card1).isEqualTo(card2);
        assertThat(card1).isNotEqualTo(card3);
        assertThat(card1).isNotEqualTo(null);
        assertThat(card1.hashCode()).isEqualTo(card2.hashCode());
        assertThat(card1.hashCode()).isNotEqualTo(card3.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        User user = new User();
        user.setId("user-1");
        user.setName("John");

        CardInfo card = new CardInfo();
        card.setId(100L);
        card.setUser(user);
        card.setNumber("1234567890123456");
        card.setHolder("John Doe");
        card.setExpirationDate(LocalDate.of(2030, 12, 31));

        String str = card.toString();
        assertThat(str).contains("100", "1234567890123456", "John Doe", "2030-12-31");
    }

    @Test
    void testUserAssociation() {
        User user = new User();
        user.setId("user-5");

        CardInfo card = new CardInfo();
        card.setUser(user);

        assertThat(card.getUser().getId()).isEqualTo("user-5");
    }
}
