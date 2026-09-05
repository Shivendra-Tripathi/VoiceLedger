package io.github.trip.shiv.vcledger.service.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a supplied "old password" does not match the user's current
 * encoded password during a password-change operation. No existing
 * exception of this kind was present in the project, so this is the
 * minimal type required.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPasswordException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidPasswordException(String message) {
        super(message);
    }
}