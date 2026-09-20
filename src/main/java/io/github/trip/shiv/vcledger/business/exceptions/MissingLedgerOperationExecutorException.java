	
package io.github.trip.shiv.vcledger.business.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MissingLedgerOperationExecutorException extends RuntimeException {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingLedgerOperationExecutorException(String message) {
        super(message);
	    }
}
	 