package io.github.trip.shiv.vcledger.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.repository.UserRepository;
import io.github.trip.shiv.vcledger.service.exception.EmailAlreadyExistsException;
import io.github.trip.shiv.vcledger.service.exception.InvalidPasswordException;
import io.github.trip.shiv.vcledger.service.exception.UserNotFoundException;

/**
 * Business logic for the shopkeeper/owner (User) entity.
 *
 * Responsibility split:
 *   UserController  -> HTTP request/response only, delegates to this service
 *   UserService      -> business rules (this class)
 *   UserRepository   -> persistence access (unmodified, reused as-is)
 *   User             -> JPA entity (unmodified)
 *
 * This class intentionally has no dependency on HttpServletRequest,
 * Authentication, or SecurityContextHolder. Callers (controllers) are
 * expected to extract the identifier — typically the email that Spring
 * Security already resolves via Authentication#getName() in this project's
 * JWT setup — and pass it in as a plain String, e.g.:
 *
 *     userService.getCurrentUser(authentication.getName());
 *
 * This keeps UserService reusable outside an HTTP context and easy to unit
 * test without mocking servlet/security objects.
 *
 * Note: the entity's `password` column is declared unique in User.java.
 * That's unusual for a hashed/encoded password field, but the entity was
 * not modified per the task constraints — it's simply worth knowing this
 * exists if two users somehow ever encode to the same hash and a save
 * fails on the unique constraint.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieve the currently authenticated user.
     *
     * @param email the authenticated principal's email, as resolved by the
     *              existing JWT/Spring Security setup (e.g. authentication.getName())
     * @throws UserNotFoundException if no user exists with that email
     */
    @Transactional(readOnly = true)
    public User getCurrentUser(String email) {
        return getUserByEmail(email);
    }

    /**
     * Retrieve a user by their primary key.
     *
     * @throws UserNotFoundException if no user exists with that id
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    /**
     * Retrieve a user by their unique email. Reuses the existing
     * UserRepository#findByEmail — no repository changes were needed.
     *
     * @throws UserNotFoundException if no user exists with that email
     */
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    /**
     * Update the authenticated user's profile.
     *
     * Only fields that actually exist on the User entity are updatable here:
     * `name` and `email`. (Note: the earlier UserController skeleton's example
     * request body included a "phone" field, but User has no such column —
     * that field is simply ignored/unsupported until the entity is extended.)
     *
     * Rules enforced:
     *   - id is never changed (it's the lookup key, never touched here).
     *   - password is never touched by this method — use changePassword() instead.
     *   - null/blank values are treated as "no change" rather than blindly
     *     overwriting existing data.
     *   - if newEmail differs from the current email, it must not already
     *     be taken by another user (email is a unique column).
     *
     * @param currentEmail the authenticated user's current email (lookup key)
     * @param newName      new display name, or null/blank to leave unchanged
     * @param newEmail     new email, or null/blank to leave unchanged
     * @throws UserNotFoundException      if currentEmail does not match an existing user
     * @throws EmailAlreadyExistsException if newEmail is already taken by another user
     */
    @Transactional
    public User updateUser(String currentEmail, String newName, String newEmail) {
        User user = getUserByEmail(currentEmail);

        if (StringUtils.hasText(newName)) {
            user.setName(newName);
        }

        if (StringUtils.hasText(newEmail) && !newEmail.equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new EmailAlreadyExistsException("Email already in use: " + newEmail);
            }
            user.setEmail(newEmail);
        }

        return userRepository.save(user);
    }

    /**
     * Change the authenticated user's password.
     *
     * Uses the project's existing PasswordEncoder bean (injected via
     * constructor) — no second encoder or auth mechanism is created here.
     *
     * @param email       the authenticated user's email (lookup key)
     * @param oldPassword the plaintext current password to verify
     * @param newPassword the plaintext new password to encode and store
     * @throws UserNotFoundException    if email does not match an existing user
     * @throws InvalidPasswordException if oldPassword does not match the stored password
     */
    @Transactional
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = getUserByEmail(email);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Create a new user account.
     *
     * This does NOT issue a JWT, authenticate the caller, or touch any
     * Spring Security configuration — it only builds and persists the User
     * row. Token issuance stays wherever your existing login/registration
     * flow already handles it; this method is the piece that flow should
     * call to actually create the row (rather than duplicating that logic).
     *
     * Rules enforced:
     *   - id is never accepted as input; it's always DB-generated (IDENTITY).
     *   - email must be unique — checked via the existing
     *     UserRepository#existsByEmail before insert, since the unique
     *     constraint alone would otherwise surface as a raw DB exception.
     *   - the raw password is immediately encoded with the project's
     *     existing PasswordEncoder bean; the plaintext is never stored,
     *     logged, or returned.
     *
     * @param name        the new user's display name
     * @param email       the new user's email (must not already exist)
     * @param rawPassword the new user's plaintext password (will be encoded)
     * @throws EmailAlreadyExistsException if email is already taken
     */
    @Transactional
    public User createUser(String name, String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already in use: " + email);
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .build();

        return userRepository.save(user);
    }

    /**
     * Cheap existence check, delegates directly to the existing repository
     * method — no new query logic needed.
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}