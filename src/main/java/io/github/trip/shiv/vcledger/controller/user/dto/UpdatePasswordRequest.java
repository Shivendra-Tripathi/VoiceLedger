package io.github.trip.shiv.vcledger.controller.user.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for PUT /api/users/me/password.
 *
 * Deliberately two fields only — currentPassword and newPassword — because
 * that's exactly what the existing UserService.changePassword(email,
 * oldPassword, newPassword) requires. No confirmNewPassword field: the
 * example three-field structure in the task prompt wasn't matched, since
 * the actual service method has no third parameter to receive it.
 *
 * If you'd like "does newPassword match confirmation" enforced, that has
 * to happen either (a) client-side before submitting, since there's
 * nothing server-side to compare it against once only newPassword is
 * sent, or (b) by adding a confirmNewPassword field here plus a
 * class-level validator (e.g. @AssertTrue on a helper method, or a custom
 * constraint) AND extending UserService.changePassword to accept/check it
 * — neither of which I've done here since it wasn't part of the existing
 * service signature and this task is DTO-only.
 *
 * No userId field: the authenticated user is resolved server-side from
 * authentication.getName(), matching how updateUser/updateUserRequest work.
 *
 * Neither field is ever intended to be logged, echoed back, or persisted
 * as-is — currentPassword is only used for
 * PasswordEncoder#matches(...), and newPassword is only ever stored after
 * PasswordEncoder#encode(...), both inside UserService.
 *
 * Password policy note: newPassword's @Size(min = 8) is a reasonable
 * baseline since no explicit password policy was specified anywhere in
 * the existing User entity or UserService. Tighten or loosen this to
 * match your actual policy if one exists elsewhere (e.g. in registration
 * validation).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "New password must be at least 8 characters")
    private String newPassword;
}