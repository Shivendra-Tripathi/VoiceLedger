package io.github.trip.shiv.vcledger.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
 
/**
 * Thrown when transaction input data fails validation — e.g. a null,
 * zero, or negative amount, or a missing transaction type. No existing
 * validation exception was present in the project, so this is the
 * minimal type required.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTransactionException extends RuntimeException {
 
    public InvalidTransactionException(String message) {
        super(message);
    }
}
 