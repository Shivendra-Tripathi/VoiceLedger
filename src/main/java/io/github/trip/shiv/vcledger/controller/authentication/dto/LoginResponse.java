package io.github.trip.shiv.vcledger.controller.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String tokenType;

}