package io.github.trip.shiv.vcledger.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a Transaction cannot be found — either because no
 * transaction with that id exists, or because it exists but belongs to a
 * customer owned by a different user. Both cases are reported identically
 * (404) for the same reason as CustomerNotFoundException: a caller must
 * never be able to distinguish "doesn't exist" from "belongs to someone
 * else's ledger".
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransactionNotFoundException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransactionNotFoundException(String message) {
        super(message);
    }
}