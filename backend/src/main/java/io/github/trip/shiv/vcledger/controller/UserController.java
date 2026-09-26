package io.github.trip.shiv.vcledger.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.github.trip.shiv.vcledger.core.dtos.authcontroller.req.UpdatePasswordRequest;
import io.github.trip.shiv.vcledger.core.dtos.usercontroller.req.UpdateUserRequest;
import io.github.trip.shiv.vcledger.core.dtos.usercontroller.res.UpdatePasswordResponse;
import io.github.trip.shiv.vcledger.core.dtos.visualpreviews.PersonInfo;
import io.github.trip.shiv.vcledger.core.utilities.SecurityUtils;
import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	
	
	private final SecurityUtils utils;
	private final UserService userService;
	

    /**
     * GET /api/users/me
     * Get the currently authenticated user's profile.
     * Requires authentication (JWT).
     */
    @GetMapping("/me")
    public ResponseEntity<PersonInfo> getCurrentUser() {
      User user = utils.getAuthenticatedUser();
    
      return ResponseEntity
    		  .ok(PersonInfo.fromShopkeeper(user));  
    }

    /**
     * PUT /api/users/me
     * Update the current user's profile.
     * Requires authentication (JWT).
     */
    @PutMapping(
    		value = "/me",
		    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
		)
    public ResponseEntity<PersonInfo> updateCurrentUser(
    		@Valid @RequestPart("user") UpdateUserRequest request,
    		@RequestPart(value="image",required = false) MultipartFile image) {
    	
        User user = utils.getAuthenticatedUser();
        user  = userService.updateUser(user.getId(), request.getName(),image);
        
        return ResponseEntity.ok(
        		PersonInfo.fromShopkeeper(user));
        
    }

    /**
     * PUT /api/users/me/password
     * Change the current user's password.
     * Requires authentication (JWT).
     */
    @PutMapping("/me/password")
    public ResponseEntity<UpdatePasswordResponse> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request) {
    	User user = utils.getAuthenticatedUser();
        userService.changePassword(user.getId(), request.getCurrentPassword(), request.getNewPassword());
        UpdatePasswordResponse response = new UpdatePasswordResponse("User's Password updated");
        return ResponseEntity.ok(response);
    }
}