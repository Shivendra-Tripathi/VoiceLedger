package io.github.trip.shiv.vcledger.service.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
* Thrown when a User cannot be found by id or email.
* No existing "not found" exception was present in the project, so this is
* the minimal type required for UserService to signal a missing user.
* @ResponseStatus maps this to 404 automatically for any controller that
* lets it propagate, without requiring a global @ExceptionHandler.
*/
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

   public UserNotFoundException(String message) {
       super(message);
   }
}
