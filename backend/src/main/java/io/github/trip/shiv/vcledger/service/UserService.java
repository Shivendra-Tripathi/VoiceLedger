
package io.github.trip.shiv.vcledger.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.github.trip.shiv.vcledger.core.exceptions.custom.business.EmailAlreadyExistsException;
import io.github.trip.shiv.vcledger.core.exceptions.custom.business.InvalidPasswordException;
import io.github.trip.shiv.vcledger.core.exceptions.custom.business.UserNotFoundException;
import io.github.trip.shiv.vcledger.core.external.imagestorage.CloudinaryUploadResult;
import io.github.trip.shiv.vcledger.core.external.imagestorage.ImageStorageService;
import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

/**
 * Provides business logic for user management.
 *
 * Handles user retrieval, profile updates, password changes, account creation,
 * and account deletion.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageStorageService imageStorageService;


    /**
     * Retrieves the currently authenticated user.
     *
     * @param email the authenticated user's email
     * @return the user associated with the email
     * @throws UserNotFoundException if no user exists with the email
     */
    @Transactional(readOnly = true)
    public User getCurrentUser(String email) {
        return getUserByEmail(email);
    }

    /**
     * Retrieves a user by their primary key.
     *
     * @param id the user's primary key
     * @return the user associated with the id
     * @throws UserNotFoundException if no user exists with the id
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    /**
     * Retrieves a user by their unique email.
     *
     * @param email the user's email
     * @return the user associated with the email
     * @throws UserNotFoundException if no user exists with the email
     */
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    /**
     * Updates the user's profile information and profile image.
     *
     * @param userId the user's primary key
     * @param newName the new user name
     * @param photoImage the new profile image
     * @return the updated user
     * @throws UserNotFoundException if no user exists with the id
     */
    @Transactional
    public User updateUser(Long userId, String newName, MultipartFile photoImage) {
        User user = getUserById(userId);

        if (StringUtils.hasText(newName)) {
            user.setName(newName);
        }


        if (photoImage != null) {
            //Delete the Old Image
            if (user.getPhotoPublicId() != null) {
                imageStorageService.deleteImage(user.getPhotoPublicId());
            }
            CloudinaryUploadResult result =
                    imageStorageService.uploadImage(photoImage, "user" + user.getId());

            user.setPhotoPublicId(result.getPublicId());
            user.setPhotoUrl(result.getSecureUrl());
        }

        return userRepository.save(user);
    }

    /**
     * Changes the user's password after validating the current password.
     *
     * @param userId the user's primary key
     * @param oldPassword the user's current password
     * @param newPassword the new password
     * @throws UserNotFoundException if no user exists with the id
     * @throws InvalidPasswordException if the current password is incorrect
     */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Creates a new user account.
     *
     * @param name the user's display name
     * @param email the user's email
     * @param rawPassword the user's plaintext password
     * @param photoImage the user's profile image
     * @return the newly created user
     * @throws EmailAlreadyExistsException if the email is already registered
     */
    @Transactional
    public User createUser(String name, String email, String rawPassword, MultipartFile photoImage) {

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException("Email already in use: " + normalizedEmail);
        }

        User user = User.builder()
                .name(name.trim())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(rawPassword))
                .build();

        user = userRepository.save(user);

        if (photoImage != null) {
            CloudinaryUploadResult result =
                    imageStorageService.uploadImage(photoImage, "user" + user.getId());

            user.setPhotoPublicId(result.getPublicId());
            user.setPhotoUrl(result.getSecureUrl());
        }

        return user;
    }

    /**
     * Deletes a user and their associated profile image.
     *
     * @param userId the user's primary key
     * @throws UserNotFoundException if no user exists with the id
     */
    @Transactional
    public void deleteUser(Long userId) {

        User user = getUserById(userId);

        if (user.getPhotoPublicId() != null) {
            imageStorageService.deleteImage(user.getPhotoPublicId());
        }

        userRepository.deleteById(userId);
    }

    /**
     * Checks whether a user exists with the given email.
     *
     * @param email the user's email
     * @return true if a user exists with the email, otherwise false
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}

