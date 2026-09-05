package io.github.trip.shiv.vcledger.controller.user.dto;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Safe, frontend-facing representation of a User.
 *
 * Fields mirror exactly what exists on the User entity, minus anything
 * sensitive:
 *   - id, name, email, createdAt  -> included, nothing to hide about these
 *   - password                     -> NEVER included
 *   - customers (the owned list)   -> NEVER included; that's a large,
 *     separate relationship that belongs in customer-specific endpoints
 *     (e.g. GET /api/customers), not bundled into every user profile response
 *
 * Built via UserService -> Controller, e.g.:
 *
 *     User user = userService.getCurrentUser(authentication.getName());
 *     UserResponse response = UserResponse.builder()
 *             .id(user.getId())
 *             .name(user.getName())
 *             .email(user.getEmail())
 *             .createdAt(user.getCreatedAt())
 *             .build();
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String name;

    private String email;

    private LocalDateTime createdAt;
}