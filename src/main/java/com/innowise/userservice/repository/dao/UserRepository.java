package com.innowise.userservice.repository.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.innowise.userservice.model.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link User} entities.
 * <p>
 * Provides CRUD operations, as well as custom methods for querying users
 * by email or by a list of IDs. Supports both JPQL and native SQL queries.
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id IN :ids")
    List<User> findUsersByIds(@Param("ids") List<Long> ids);

    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    Optional<User> findUserByEmailNative(@Param("email") String email);

    void deleteByEmail(String email);
}

