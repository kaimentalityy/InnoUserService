package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.mapper.CardInfoMapper;
import com.innowise.userservice.model.dto.CardInfoDto;
import com.innowise.userservice.model.entity.CardInfo;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.dao.CardInfoRepository;
import com.innowise.userservice.repository.dao.UserRepository;
import com.innowise.userservice.repository.specification.CardInfoSpecification;
import com.innowise.userservice.service.CardInfoServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "cards")
public class CardInfoService implements CardInfoServiceInterface {

    private final CardInfoRepository cardInfoRepository;
    private final CardInfoMapper cardInfoMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    @CachePut(key = "#result.id")
    public CardInfoDto create(CardInfoDto dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User", "id", dto.userId()));

        CardInfo card = cardInfoMapper.toEntity(dto);
        card.setUser(user);
        return cardInfoMapper.toDto(cardInfoRepository.save(card));
    }

    @Override
    @Transactional
    public CardInfoDto update(Long id, CardInfoDto dto) {
        CardInfo card = cardInfoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Card", "id", id));
        cardInfoMapper.updateEntity(dto, card);
        return cardInfoMapper.toDto(cardInfoRepository.save(card));
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public void delete(Long id) {
        if (!cardInfoRepository.existsById(id)) {
            throw new EntityNotFoundException("Card", "id", id);
        }
        cardInfoRepository.deleteById(id);
    }

    @Override
    @Cacheable(key = "#id")
    public CardInfoDto findById(Long id) {
        CardInfo card = cardInfoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Card", "id", id));
        return cardInfoMapper.toDto(card);
    }

    @Override
    public List<CardInfoDto> findByIds(List<Long> ids) {
        return cardInfoRepository.findAllById(ids)
                .stream()
                .map(cardInfoMapper::toDto)
                .toList();
    }

    @Override
    public Page<CardInfoDto> searchCards(String userId, String cardNumber, String cardHolder, Pageable pageable) {
        Specification<CardInfo> spec = Specification.where(null);
        if (userId != null)
            spec = spec.and(CardInfoSpecification.hasUserId(userId));
        if (cardNumber != null)
            spec = spec.and(CardInfoSpecification.hasCardNumber(cardNumber));
        if (cardHolder != null)
            spec = spec.and(CardInfoSpecification.hasCardHolder(cardHolder));
        return cardInfoRepository.findAll(spec, pageable).map(cardInfoMapper::toDto);
    }

    @CacheEvict(key = "#id")
    public void evictCardFromCache(Long id) {
    }
}
