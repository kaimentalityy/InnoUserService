package com.innowise.userservice.service;

import com.innowise.userservice.model.dto.CardInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for CardInfo entity.
 * Extends generic CRUD operations from CrudService.
 */
public interface CardInfoServiceInterface extends CrudService<CardInfoDto, Long> {

    /**
     * Finds multiple cards by their IDs.
     *
     * @param ids list of card IDs
     * @return list of CardInfoDto
     */
    List<CardInfoDto> findByIds(List<Long> ids);

    /**
     * Searches cards based on optional filters.
     *
     * @param userId     user ID to filter cards
     * @param cardNumber card number to filter
     * @param cardHolder cardholder name to filter
     * @param pageable   pagination information
     * @return page of CardInfoDto
     */
    Page<CardInfoDto> searchCards(String userId, String cardNumber, String cardHolder, Pageable pageable);

    /**
     * Evicts a card from cache manually.
     *
     * @param id card ID to evict
     */
    void evictCardFromCache(Long id);
}
