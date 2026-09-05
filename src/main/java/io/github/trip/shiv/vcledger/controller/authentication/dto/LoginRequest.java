package io.github.trip.shiv.vcledger.controller.authentication.dto;

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