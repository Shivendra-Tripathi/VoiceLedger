package io.github.trip.shiv.vcledger.core.dtos.authcontroller.req;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Request body for POST /api/auth/login
 */
@Getter
@AllArgsConstructor
public class LoginRequest {

    private String email;
    private String password;
   
}