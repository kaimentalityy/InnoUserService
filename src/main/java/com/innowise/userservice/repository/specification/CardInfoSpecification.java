package com.innowise.userservice.repository.specification;

import com.innowise.userservice.model.entity.CardInfo;
import org.springframework.data.jpa.domain.Specification;

public class CardInfoSpecification {

    public static Specification<CardInfo> hasUserId(String userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<CardInfo> hasCardNumber(String number) {
        return (root, query, cb) -> cb.like(root.get("number"), "%" + number + "%");
    }

    public static Specification<CardInfo> hasCardHolder(String holder) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("holder")), "%" + holder.toLowerCase() + "%");
    }
}
