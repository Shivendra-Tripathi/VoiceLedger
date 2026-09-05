package io.github.trip.shiv.vcledger.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.trip.shiv.vcledger.controller.authentication.dto.UpdatePasswordRequest;
import io.github.trip.shiv.vcledger.controller.user.dto.UpdatePasswordResponse;
import io.github.trip.shiv.vcledger.controller.user.dto.UpdateUserRequest;
import io.github.trip.shiv.vcledger.controller.user.dto.UpdateUserResponse;
import io.github.trip.shiv.vcledger.controller.user.dto.UserResponse;
import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.security.SecurityUtils;
import io.github.trip.shiv.vcledger.service.UserService;

/**
 * UserController
 *
 * Skeleton REST controller for user profile management.
 * NOTE: No business logic, service calls, or repository calls are implemented here.
 * All endpoints return dummy responses for API-contract testing purposes only.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
	
	
	private final SecurityUtils utils;
	private final UserService userService;
	
	public UserController(SecurityUtils utils,
			UserService userService) {
		this.utils = utils;
		this.userService = userService;
	}

    /**
     * GET /api/users/me
     * Get the currently authenticated user's profile.
     * Requires authentication (JWT).
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
      User user = utils.getAuthenticatedUser();
      UserResponse userResponse = new UserResponse(user.getId(),user.getName(),user.getEmail(),user.getCreatedAt());
      return ResponseEntity
    		  .ok(userResponse);  
    }

    /**
     * PUT /api/users/me
     * Update the current user's profile.
     * Requires authentication (JWT).
     */
    @PutMapping("/me")
    public ResponseEntity<UpdateUserResponse> updateCurrentUser(@RequestBody UpdateUserRequest request) {
        User user = utils.getAuthenticatedUser();
        userService.updateUser(user.getEmail(), request.getName(), request.getEmail());
        UpdateUserResponse response = new UpdateUserResponse("User updated");
        return ResponseEntity.ok(response);
        
    }

    /**
     * PUT /api/users/me/password
     * Change the current user's password.
     * Requires authentication (JWT).
     */
    @PutMapping("/me/password")
    public ResponseEntity<UpdatePasswordResponse> updatePassword(@RequestBody UpdatePasswordRequest request,
                                                    Authentication authentication) {
    	User user = utils.getAuthenticatedUser();
        userService.changePassword(user.getEmail(), request.getCurrentPassword(), request.getNewPassword());
        UpdatePasswordResponse response = new UpdatePasswordResponse("User's Password updated");
        return ResponseEntity.ok(response);
    }
}