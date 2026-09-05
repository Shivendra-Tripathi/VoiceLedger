package io.github.trip.shiv.vcledger.controller.user.dto;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for a successful PUT /api/users/me/password.
 *
 * Intentionally minimal — a single confirmation message, nothing else.
 * UserService.changePassword(...) returns void, and there's no additional
 * state worth reporting back (no token to refresh, no new password to
 * echo). Never include currentPassword, newPassword, or any derived
 * password data here.
 *
 * Example usage in the controller:
 *
 *     userService.changePassword(authentication.getName(),
 *             request.getCurrentPassword(), request.getNewPassword());
 *     return ResponseEntity.ok(
 *             new UpdatePasswordResponse("Password updated successfully"));
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserResponse {

    private String message;
}