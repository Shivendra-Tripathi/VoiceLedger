package io.github.trip.shiv.vcledger.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.trip.shiv.vcledger.entity.User;

/**
 * Repository for the shopkeeper/owner entity.
 *
 * Beyond basic CRUD (inherited from JpaRepository), the methods here cover
 * the two things a voice/REST ledger app will realistically need around
 * User: authenticating/looking a user up by email, and cheaply checking
 * whether an email is already taken during registration.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Look up a user by their unique email — the natural key for login,
     * authentication, and "find the current logged-in user" flows.
     */
    Optional<User> findByEmail(String email);

    /**
     * Cheap existence check for registration validation, avoids pulling
     * back a full User just to check for a duplicate email.
     */
    boolean existsByEmail(String email);
}