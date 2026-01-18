package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.dto.UserRegisterDto;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.model.enums.UserRole;
import com.innowise.userservice.repository.dao.UserRepository;
import com.innowise.userservice.repository.specification.UserSpecification;
import com.innowise.userservice.service.UserServiceInterface;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "users")
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CardInfoService cardInfoService;

    @Override
    @Transactional
    @CachePut(key = "#result.id")
    public UserDto create(UserDto dto) {
        User user = userMapper.toUser(dto);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", "id", id));
        userMapper.updateUserFromDto(dto, user);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", "id", id));

        user.getCards().forEach(card -> cardInfoService.evictCardFromCache(card.getId()));

        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(key = "#id")
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", "id", id));
        return userMapper.toUserDto(user);
    }

    @Transactional
    public void deleteByEmail(String email) {
        userRepository.deleteByEmail(email);
    }

    public UserDto createFromAuth(UserRegisterDto dto) {
        User user = new User();
        user.setName(dto.name());
        user.setSurname(dto.surname());
        user.setBirthDate(dto.birthDate());
        user.setEmail(dto.email());
        user.setCards(new ArrayList<>());

        if (dto.role() != null && !dto.role().isBlank()) {
            String roleName = dto.role().startsWith("ROLE_") ? dto.role() : "ROLE_" + dto.role();
            user.setRole(UserRole.valueOf(roleName));
        } else {
            user.setRole(UserRole.ROLE_USER);
        }

        User saved = userRepository.save(user);
        return userMapper.toUserDto(saved);
    }

    @Override
    public List<UserDto> findByIds(List<Long> ids) {
        return userRepository.findAllById(ids)
                .stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User", "email", email));
        return userMapper.toUserDto(user);
    }

    @Override
    public Page<UserDto> searchUsers(String name, String surname, String email, Pageable pageable) {
        Specification<User> spec = Specification.where(null);
        if (name != null)
            spec = spec.and(UserSpecification.hasName(name));
        if (surname != null)
            spec = spec.and(UserSpecification.hasSurname(surname));
        if (email != null)
            spec = spec.and(UserSpecification.hasEmail(email));
        return userRepository.findAll(spec, pageable).map(userMapper::toUserDto);
    }
}
