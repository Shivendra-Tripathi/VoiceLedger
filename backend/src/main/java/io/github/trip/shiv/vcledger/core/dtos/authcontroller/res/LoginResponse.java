package io.github.trip.shiv.vcledger.core.dtos.authcontroller.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String tokenType;

}