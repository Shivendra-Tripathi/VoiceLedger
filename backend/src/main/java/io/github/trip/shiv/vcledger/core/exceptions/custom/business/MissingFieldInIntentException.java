package io.github.trip.shiv.vcledger.core.exceptions.custom.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MissingFieldInIntentException extends RuntimeException {

public MissingFieldInIntentException(String message) {
    super(message);
    }
}
 