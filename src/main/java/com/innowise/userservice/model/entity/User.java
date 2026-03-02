package com.innowise.userservice.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a user in the system.
 * <p>
 * Each user can have multiple associated {@link CardInfo} objects.
 * Cascade operations are enabled so that changes to a user automatically
 * propagate to their cards.
 * </p>
 * <p>
 * User roles are managed by Keycloak and extracted from JWT tokens during
 * authentication.
 * This entity only stores user profile information (name, surname, birthDate,
 * email) and card information.
 * </p>
 */
@Entity
@Data
@Table(name = "users")
public class User {

    /**
     * Primary key of the user (Keycloak ID).
     */
    @Id
    private String id;

    /**
     * First name of the user.
     * Cannot be null.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Last name of the user.
     * Cannot be null.
     */
    @Column(name = "surname", nullable = false)
    private String surname;

    /**
     * Birthdate of the user.
     * Cannot be null.
     */
    @Column(name = "birth_date", nullable = false)
    private java.time.LocalDate birthDate;

    /**
     * Email of the user.
     * Must be unique and cannot be null.
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * List of cards associated with this user.
     * Cascade operations ensure that updates/deletes propagate.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardInfo> cards = new ArrayList<>();

}
