package io.github.trip.shiv.vcledger.core.exceptions.custom.business;



public class PendingOperationNotFoundException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PendingOperationNotFoundException(String message) {
        super(message);
    }
}