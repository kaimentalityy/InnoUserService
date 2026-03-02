package com.innowise.userservice.service;

import com.innowise.userservice.BaseIntegrationTest;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.model.dto.CardInfoDto;
import com.innowise.userservice.model.entity.CardInfo;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.dao.CardInfoRepository;
import com.innowise.userservice.repository.dao.UserRepository;
import com.innowise.userservice.service.impl.CardInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
class CardInfoServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CardInfoService cardInfoService;

    @Autowired
    private CardInfoRepository cardInfoRepository;

    @Autowired
    private UserRepository userRepository;

    private User defaultUser;

    @BeforeEach
    void init() {
        defaultUser = createTestUser("Default", "User", "default@example.com");
    }

    @Test
    void testCreateCardInfo_success() {
        CardInfoDto dto = new CardInfoDto(
                null,
                defaultUser.getId(),
                "1234567890123456",
                "Default User",
                LocalDate.of(2030, 12, 31));

        CardInfoDto created = cardInfoService.create(dto);

        assertThat(created).isNotNull();
        assertThat(created.number()).isEqualTo("1234567890123456");
        assertThat(created.holder()).isEqualTo("Default User");
    }

    @Test
    void testCreateCardInfo_userNotFound() {
        CardInfoDto dto = new CardInfoDto(
                null,
                "non-existent",
                "0000111122223333",
                "Ghost Holder",
                LocalDate.of(2030, 12, 31));

        assertThrows(EntityNotFoundException.class, () -> cardInfoService.create(dto));
    }

    @Test
    void testFindById_success() {
        CardInfo card = createTestCard("1111222233334444", defaultUser);
        CardInfoDto dto = cardInfoService.findById(card.getId());

        assertThat(dto.number()).isEqualTo("1111222233334444");
        assertThat(dto.holder()).isEqualTo(card.getHolder());
    }

    @Test
    void testFindById_notFound() {
        assertThrows(EntityNotFoundException.class, () -> cardInfoService.findById(-1L));
    }

    @Test
    void testFindByIds_success() {
        CardInfo card1 = createTestCard("1111", defaultUser);
        CardInfo card2 = createTestCard("2222", defaultUser);

        List<CardInfoDto> results = cardInfoService.findByIds(List.of(card1.getId(), card2.getId()));

        assertThat(results).hasSize(2);
        assertThat(results.stream().map(CardInfoDto::number))
                .contains("1111", "2222");
    }

    @Test
    void testFindByIds_partialExisting() {
        CardInfo card = createTestCard("9999", defaultUser);
        List<CardInfoDto> results = cardInfoService.findByIds(List.of(card.getId(), -1L));
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().number()).isEqualTo("9999");
    }

    @Test
    void testSearchCards_byUserId() {
        createTestCard("1111000011110001", defaultUser);
        createTestCard("2222000022220002", defaultUser);

        Pageable pageable = PageRequest.of(0, 10);
        Page<CardInfoDto> cards = cardInfoService.searchCards(defaultUser.getId(), null, null, pageable);

        assertThat(cards.getContent()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void testSearchCards_byCardNumber() {
        CardInfo card = createTestCard("1234123412341234", defaultUser);
        Page<CardInfoDto> page = cardInfoService.searchCards(null, "1234", null, PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().getFirst().number()).isEqualTo(card.getNumber());
    }

    @Test
    void testSearchCards_byCardHolder() {
        createTestCard("7777888899990000", defaultUser);
        Page<CardInfoDto> page = cardInfoService.searchCards(null, null, "Default User", PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().getFirst().holder()).contains("Default User");
    }

    @Test
    void testSearchCards_combinedFilters() {
        createTestCard("1000100010001000", defaultUser);
        Page<CardInfoDto> page = cardInfoService.searchCards(defaultUser.getId(), "1000", "Default User",
                PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void testUpdateCardInfo_success() {
        CardInfo card = createTestCard("9999888877776666", defaultUser);

        CardInfoDto dto = new CardInfoDto(
                card.getId(),
                defaultUser.getId(),
                "1111222233334444",
                "Updated Holder",
                LocalDate.of(2035, 1, 1));

        CardInfoDto updated = cardInfoService.update(card.getId(), dto);

        assertThat(updated.number()).isEqualTo("1111222233334444");
        assertThat(updated.holder()).isEqualTo("Updated Holder");
    }

    @Test
    void testUpdateCardInfo_notFound() {
        CardInfoDto dto = new CardInfoDto(-1L, defaultUser.getId(), "9999", "Holder", LocalDate.now());
        assertThrows(EntityNotFoundException.class, () -> cardInfoService.update(-1L, dto));
    }

    @Test
    void testDeleteCardInfo_success() {
        CardInfo card = createTestCard("5555444433332222", defaultUser);
        cardInfoService.delete(card.getId());
        assertThat(cardInfoRepository.findById(card.getId())).isEmpty();
    }

    @Test
    void testDeleteCardInfo_notFound() {
        assertThrows(EntityNotFoundException.class, () -> cardInfoService.delete(999999L));
    }

    @Test
    void testEvictCardFromCache_noError() {
        cardInfoService.evictCardFromCache(1L);
    }

    @Test
    void testCreateThenFetchConsistency() {
        CardInfoDto dto = new CardInfoDto(null, defaultUser.getId(), "5656565656565656", "Sync User",
                LocalDate.of(2033, 5, 5));
        CardInfoDto created = cardInfoService.create(dto);
        CardInfoDto found = cardInfoService.findById(created.id());
        assertThat(found.number()).isEqualTo("5656565656565656");
    }

    @Test
    void testRepositoryStateAfterCreate() {
        long before = cardInfoRepository.count();
        cardInfoService.create(new CardInfoDto(null, defaultUser.getId(), "0000111122223333", "Count Check",
                LocalDate.of(2031, 11, 11)));
        assertThat(cardInfoRepository.count()).isEqualTo(before + 1);
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

    private CardInfo createTestCard(String cardNumber, User user) {
        CardInfo card = new CardInfo();
        card.setNumber(cardNumber);
        card.setHolder(user.getName() + " " + user.getSurname());
        card.setExpirationDate(LocalDate.of(2030, 12, 31));
        card.setUser(user);
        return cardInfoRepository.save(card);
    }
}
