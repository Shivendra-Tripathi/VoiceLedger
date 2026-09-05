package io.github.trip.shiv.vcledger.service.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when attempting to change a User's email to one that is already
 * taken (email is a unique column on User). No existing conflict exception
 * was present in the project, so this is the minimal type required.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmailAlreadyExistsException(String message) {
        super(message);
    }
}