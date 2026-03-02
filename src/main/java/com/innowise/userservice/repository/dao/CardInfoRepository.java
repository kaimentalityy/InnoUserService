package com.innowise.userservice.repository.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.innowise.userservice.model.entity.CardInfo;

import java.util.List;

/**
 * Repository interface for {@link CardInfo} entities.
 * <p>
 * Provides CRUD operations, as well as custom methods for querying cards
 * by user ID, multiple card IDs, or card number. Supports both JPQL and native
 * SQL queries.
 * </p>
 */
@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Long>, JpaSpecificationExecutor<CardInfo> {

    List<CardInfo> findByUserId(String userId);

    @Query("SELECT c FROM CardInfo c WHERE c.id IN :ids")
    List<CardInfo> findCardsByIds(@Param("ids") List<Long> ids);

    @Query(value = "SELECT * FROM card_info WHERE number = :cardNumber", nativeQuery = true)
    CardInfo findByCardNumberNative(@Param("cardNumber") String cardNumber);

}
