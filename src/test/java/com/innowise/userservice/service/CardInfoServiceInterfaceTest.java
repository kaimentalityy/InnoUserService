package com.innowise.userservice.service;

import com.innowise.userservice.model.dto.CardInfoDto;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardInfoServiceInterfaceTest {

    @Test
    void testCardInfoServiceInterfaceMethodsExist() {
        
        CardInfoServiceInterface service = new TestCardInfoServiceInterfaceImpl();
        
        
        List<Long> ids = List.of(1L, 2L, 3L);
        List<CardInfoDto> cards = service.findByIds(ids);
        assertNotNull(cards);
        
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardInfoDto> page = service.searchCards("user123", "1234567890123456", "Test User", pageable);
        assertNotNull(page);
        
        
        assertDoesNotThrow(() -> service.evictCardFromCache(1L));
    }

    @Test
    void testFindByIdsWithEmptyList() {
        CardInfoServiceInterface service = new TestCardInfoServiceInterfaceImpl();
        List<CardInfoDto> result = service.findByIds(List.of());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByIdsWithNullList() {
        CardInfoServiceInterface service = new TestCardInfoServiceInterfaceImpl();
        assertThrows(NullPointerException.class, () -> service.findByIds(null));
    }

    @Test
    void testSearchCardsWithNullParameters() {
        CardInfoServiceInterface service = new TestCardInfoServiceInterfaceImpl();
        Pageable pageable = PageRequest.of(0, 10);
        
        
        Page<CardInfoDto> result1 = service.searchCards(null, null, null, pageable);
        assertNotNull(result1);
        
        
        Page<CardInfoDto> result2 = service.searchCards("user123", null, null, pageable);
        assertNotNull(result2);
        
        Page<CardInfoDto> result3 = service.searchCards(null, "1234567890123456", null, pageable);
        assertNotNull(result3);
        
        Page<CardInfoDto> result4 = service.searchCards(null, null, "Test User", pageable);
        assertNotNull(result4);
    }

    @Test
    void testSearchCardsWithEmptyStrings() {
        CardInfoServiceInterface service = new TestCardInfoServiceInterfaceImpl();
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<CardInfoDto> result = service.searchCards("", "", "", pageable);
        assertNotNull(result);
    }

    @Test
    void testSearchCardsWithValidParameters() {
        CardInfoServiceInterface service = new TestCardInfoServiceInterfaceImpl();
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<CardInfoDto> result = service.searchCards("user123", "1234567890123456", "Test User", pageable);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testEvictCardFromCache() {
        CardInfoServiceInterface service = new TestCardInfoServiceInterfaceImpl();
        
        
        assertDoesNotThrow(() -> service.evictCardFromCache(1L));
        
        
        assertDoesNotThrow(() -> service.evictCardFromCache(null));
        
        
        assertDoesNotThrow(() -> service.evictCardFromCache(0L));
    }

    
    private static class TestCardInfoServiceInterfaceImpl implements CardInfoServiceInterface {
        
        @Override
        public List<CardInfoDto> findByIds(List<Long> ids) {
            if (ids == null) {
                throw new NullPointerException("ids cannot be null");
            }
            return ids.stream()
                .map(id -> new CardInfoDto(id, "user-" + id, "1234567890123456", 
                    "Test User", LocalDate.of(2025, 12, 31)))
                .toList();
        }

        @Override
        public Page<CardInfoDto> searchCards(String userId, String cardNumber, String cardHolder, Pageable pageable) {
            CardInfoDto card = new CardInfoDto(1L, 
                userId != null ? userId : "default-user",
                cardNumber != null ? cardNumber : "1234567890123456",
                cardHolder != null ? cardHolder : "Default User", 
                LocalDate.of(2025, 12, 31));
            
            return new PageImpl<>(List.of(card), pageable, 1);
        }

        @Override
        public void evictCardFromCache(Long id) {
            
        }

        @Override
        public CardInfoDto create(CardInfoDto dto) {
            return new CardInfoDto(1L, dto.userId(), dto.number(), dto.holder(), dto.expirationDate());
        }

        @Override
        public CardInfoDto update(Long id, CardInfoDto dto) {
            return new CardInfoDto(id, dto.userId(), dto.number(), dto.holder(), dto.expirationDate());
        }

        @Override
        public void delete(Long id) {
            
        }

        @Override
        public CardInfoDto findById(Long id) {
            return new CardInfoDto(id, "user123", "1234567890123456", "Test User", LocalDate.of(2025, 12, 31));
        }
    }
}
