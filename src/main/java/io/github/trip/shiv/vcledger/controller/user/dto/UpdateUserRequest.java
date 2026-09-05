package io.github.trip.shiv.vcledger.controller.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for PUT /api/users/me.
 *
 * Only `name` and `email` are included — those are the only two fields on
 * the User entity that a profile-update operation should touch (`id` is
 * never client-settable, `password` has its own dedicated endpoint/DTO,
 * `createdAt` is server-managed, and `customers` isn't a profile field at
 * all). There is deliberately no `userId` field: UserService.updateUser
 * takes the authenticated user's email from the security context
 * (authentication.getName()), not from the request body, so ownership
 * can't be spoofed by the client.
 *
 * Both fields are optional to support partial updates — UserService's
 * existing updateUser(currentEmail, newName, newEmail) already treats a
 * null/blank value as "no change" rather than clobbering existing data.
 * That's why @NotBlank is intentionally NOT used here: requiring every
 * field on every request would break partial updates (e.g. changing only
 * the name without resending the email). Validation below only applies
 * length/format constraints to whatever value IS supplied.
 *
 * Note on length limits: matched to the User entity's actual column
 * definitions (name: length = 100, email: length = 150), so a request
 * that would fail the DB constraint fails validation earlier instead.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Email(message = "Email must be a valid email address")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;
}