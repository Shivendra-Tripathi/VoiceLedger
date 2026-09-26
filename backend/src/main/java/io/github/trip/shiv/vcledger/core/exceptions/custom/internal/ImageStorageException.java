package io.github.trip.shiv.vcledger.core.exceptions.custom.internal;


/** Wraps any failure talking to Cloudinary (upload or delete). */
public class ImageStorageException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ImageStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}