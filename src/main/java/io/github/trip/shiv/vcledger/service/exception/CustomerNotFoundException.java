package io.github.trip.shiv.vcledger.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
 
/**
 * Thrown when a Customer cannot be found — either because no customer with
 * that id exists at all, or because it exists but belongs to a different
 * user. Both cases are deliberately reported identically (404) so that a
 * caller can never distinguish "doesn't exist" from "belongs to someone
 * else"; that distinction would leak the existence of other users' data.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomerNotFoundException extends RuntimeException {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CustomerNotFoundException(String message) {
        super(message);
    }
}
 