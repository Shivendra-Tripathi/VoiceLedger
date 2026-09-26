package io.github.trip.shiv.vcledger.core.dtos.authcontroller.req;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Request body for PUT /api/auth/password
 */
@Getter
@AllArgsConstructor
public class UpdatePasswordRequest {

    private String currentPassword;
    private String newPassword;


}