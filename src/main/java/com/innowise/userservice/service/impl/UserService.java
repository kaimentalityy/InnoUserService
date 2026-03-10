package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.EntityAlreadyExistsException;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.AuthDto;
import com.innowise.userservice.model.dto.AuthResponseDto;
import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.dto.UserRegisterDto;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.dao.UserRepository;
import com.innowise.userservice.repository.specification.UserSpecification;
import com.innowise.userservice.service.UserServiceInterface;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Qualifier;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "users")
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CardInfoService cardInfoService;

    private final Counter usersCreatedCounter;
    private final Counter usersUpdatedCounter;
    private final Counter usersDeletedCounter;
    private final Timer userOperationTimer;
    @Qualifier("adminKeycloak")
    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    private static UserRepresentation getUserRepresentation(String email, String name, String surname) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(name);
        user.setLastName(surname);
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setRequiredActions(Collections.emptyList());
        return user;
    }

    private boolean isValidUUID(String id) {
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    @Transactional
    @CachePut(key = "#result.id")
    public UserDto create(UserDto dto) {
        return userOperationTimer.record(() -> {
            User user = userMapper.toUser(dto);
            UserDto saved = userMapper.toUserDto(userRepository.save(user));
            usersCreatedCounter.increment();
            return saved;
        });
    }

    @Transactional
    public AuthResponseDto register(AuthDto request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EntityAlreadyExistsException();
        }

        boolean existsInKeycloak = !keycloak.realm(realm).users()
                .searchByEmail(request.email(), true)
                .isEmpty();
        if (existsInKeycloak) {
            throw new EntityAlreadyExistsException();
        }

        String keycloakId = null;
        try {
            UserRepresentation user = getUserRepresentation(request.email(), request.name(), request.surname());
            try (Response response = keycloak.realm(realm).users().create(user)) {
                if (response.getStatus() != 201) {
                    throw new RuntimeException("Keycloak error: " + response.getStatusInfo().getReasonPhrase());
                }
                keycloakId = CreatedResponseUtil.getCreatedId(response);
            }

            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(request.password());
            keycloak.realm(realm).users().get(keycloakId).resetPassword(passwordCred);

            RoleRepresentation userRole = keycloak.realm(realm).roles().get("user").toRepresentation();
            keycloak.realm(realm).users().get(keycloakId).roles().realmLevel()
                    .add(Collections.singletonList(userRole));

            UserRegisterDto userRequest = new UserRegisterDto(request.name(), request.surname(),
                    request.birthDate(), request.email());
            UserDto createdUser = createFromAuth(userRequest, keycloakId);

            return new AuthResponseDto(createdUser.id(), null, createdUser.email(),
                    createdUser.name(), createdUser.surname(), createdUser.birthDate(), null);

        } catch (EntityAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            if (keycloakId != null) {
                try {
                    keycloak.realm(realm).users().get(keycloakId).remove();
                } catch (Exception rollbackEx) {
                    log.error("Failed to rollback Keycloak user", rollbackEx);
                }
            }
            throw new RuntimeException("Registration failed", e);
        }
    }

    @Override
    @Transactional
    public UserDto update(String id, UserDto dto) {
        return userOperationTimer.record(() -> {
            User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", "id", id));
            userMapper.updateUserFromDto(dto, user);
            UserDto updated = userMapper.toUserDto(userRepository.save(user));
            usersUpdatedCounter.increment();
            return updated;
        });
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public void delete(String id) {
        userOperationTimer.record(() -> {
            User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", "id", id));

            user.getCards().forEach(card -> cardInfoService.evictCardFromCache(card.getId()));

            userRepository.delete(user);
            usersDeletedCounter.increment();
        });
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(key = "#id")
    public UserDto findById(String id) {
        log.info("Finding user by ID: '{}' (length: {}, format: UUID: {})",
                id, id.length(), isValidUUID(id));

        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", "id", id));
        return userMapper.toUserDto(user);
    }

    @Transactional
    public void deleteByEmail(String email) {
        userRepository.deleteByEmail(email);
    }

    public UserDto createFromAuth(UserRegisterDto dto, String keycloakId) {
        User user = new User();
        user.setId(keycloakId);
        user.setName(dto.name());
        user.setSurname(dto.surname());
        user.setBirthDate(dto.birthDate());
        user.setEmail(dto.email());
        user.setCards(new ArrayList<>());

        User saved = userRepository.save(user);
        return userMapper.toUserDto(saved);
    }

    @Override
    public List<UserDto> findByIds(List<String> ids) {
        return userRepository.findAllById(ids).stream().map(userMapper::toUserDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
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
