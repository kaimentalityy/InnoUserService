package com.innowise.userservice.repository;

import com.innowise.userservice.BaseIntegrationTest;
import com.innowise.userservice.model.entity.CardInfo;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.dao.CardInfoRepository;
import com.innowise.userservice.repository.dao.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class CardInfoRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CardInfoRepository cardInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindById() {
        User user = userRepository.save(createUser("Kiryl", "Savenka", "kiryl.savenka@innowise.com"));

        CardInfo card = createCard(user, "1234567890123456");
        CardInfo saved = cardInfoRepository.save(card);

        Optional<CardInfo> retrieved = cardInfoRepository.findById(saved.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getNumber()).isEqualTo("1234567890123456");
        assertThat(retrieved.get().getHolder()).isEqualTo("Kiryl Savenka");
    }

    @Test
    void testFindByUserId() {
        User user = userRepository.save(createUser("Dima", "Kosoy", "dima@kosoy.com"));
        cardInfoRepository.saveAll(List.of(
                createCard(user, "1111222233334444"),
                createCard(user, "5555666677778888")));

        List<CardInfo> cards = cardInfoRepository.findByUserId(user.getId());
        assertThat(cards).hasSize(2);
        assertThat(cards).extracting(CardInfo::getNumber)
                .contains("1111222233334444", "5555666677778888");
    }

    @Test
    void testFindCardsByIds() {
        User user = userRepository.save(createUser("Gleb", "Gluhoy", "gleb@gluhoy.com"));
        CardInfo c1 = cardInfoRepository.save(createCard(user, "0000111122223333"));
        CardInfo c2 = cardInfoRepository.save(createCard(user, "4444555566667777"));

        List<CardInfo> cards = cardInfoRepository.findCardsByIds(List.of(c1.getId(), c2.getId()));
        assertThat(cards).hasSize(2);
    }

    @Test
    void testFindByCardNumberNative() {
        User user = userRepository.save(createUser("Ivan", "Petrov", "ivan@petrov.com"));
        cardInfoRepository.save(createCard(user, "9999000011112222"));

        CardInfo found = cardInfoRepository.findByCardNumberNative("9999000011112222");
        assertThat(found).isNotNull();
        assertThat(found.getHolder()).isEqualTo("Ivan Petrov");
    }

    @Test
    void testFindByCardNumberNative_NotFound() {
        CardInfo found = cardInfoRepository.findByCardNumberNative("0000000000000000");
        assertThat(found).isNull();
    }

    @Test
    void testFindCardsByEmptyIds() {
        List<CardInfo> cards = cardInfoRepository.findCardsByIds(List.of());
        assertThat(cards).isEmpty();
    }

    @Test
    void testCascadeDeleteUserAlsoDeletesCards() {
        User user = createUser("Alexander", "Hromoy", "alex@hromoy.com");
        user = userRepository.save(user);
        userRepository.flush();
        
        CardInfo card = createCard(user, "9999888877776666");
        card = cardInfoRepository.save(card); 
        user.getCards().add(card); 
        user = userRepository.save(user); 
        userRepository.flush();
        
        Long cardId = card.getId();
        userRepository.delete(user);
        userRepository.flush();

        assertThat(cardInfoRepository.findById(cardId)).isEmpty();
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

    private CardInfo createCard(User user, String number) {
        CardInfo card = new CardInfo();
        card.setUser(user);
        card.setNumber(number);
        card.setHolder(user.getName() + " " + user.getSurname());
        card.setExpirationDate(LocalDate.of(2030, 12, 31));
        return card;
    }
}
