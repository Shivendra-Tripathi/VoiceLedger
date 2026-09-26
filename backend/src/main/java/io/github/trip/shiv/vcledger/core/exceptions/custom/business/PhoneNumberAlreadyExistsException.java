package io.github.trip.shiv.vcledger.core.exceptions.custom.business;



import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when attempting to change a User's email to one that is already
 * taken (email is a unique column on User). No existing conflict exception
 * was present in the project, so this is the minimal type required.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class PhoneNumberAlreadyExistsException extends RuntimeException {

    public PhoneNumberAlreadyExistsException(String message) {
        super(message);
    }
}