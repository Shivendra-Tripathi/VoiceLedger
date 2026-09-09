package io.github.trip.shiv.vcledger.controller.authentication;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.trip.shiv.vcledger.controller.authentication.dto.LoginRequest;
import io.github.trip.shiv.vcledger.controller.authentication.dto.LoginResponse;
import io.github.trip.shiv.vcledger.controller.authentication.dto.RegisterRequest;
import io.github.trip.shiv.vcledger.controller.authentication.dto.RegisterResponse;
import io.github.trip.shiv.vcledger.controller.authentication.dto.UpdatePasswordRequest;
import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.security.SecurityUtils;
import io.github.trip.shiv.vcledger.service.AuthService;
import jakarta.validation.Valid;



/**
 * Authentication endpoints for the Voice-Controlled NLP Ledger System.
 *
 * NOTE: These are endpoint stubs only. No auth logic, password hashing,
 * token issuance/validation, or session handling has been implemented yet.
 * Each method exists purely to define the API surface (route, HTTP verb,
 * request/response shape) so the frontend and other layers can be built
 * against a stable contract.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	
	  private final AuthService authService;
	  private final SecurityUtils securityUtils;

	    public AuthController(AuthService authService , SecurityUtils securityUtils) {
	        this.authService = authService;
	        this.securityUtils = securityUtils;
	    }

    /**
     * Register a new shopkeeper/owner account.
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {
    	
    
        User user = authService.register(request);
        
        RegisterResponse response = new RegisterResponse(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    /**
     * Authenticate a user and issue access/refresh tokens.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    	
    	
    	return ResponseEntity
    			.status(HttpStatus.ACCEPTED)
    			.body(authService.login(request));
    }

   

//    /**
//     * Invalidate the current session/refresh token.
//     */
//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
//        return ResponseEntity
//        .status(HttpStatus.ACCEPTED)
//        .body("LogOut Done");
//    }

//    /**
//     * Return the currently authenticated user's profile.
//     */
//    @GetMapping("/me")
//    public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authorization) {
//        // TODO: implement fetch-current-user logic
//        throw new UnsupportedOperationException("Not implemented yet");
//    }

    /**
     * Update the currently authenticated user's password.
     */
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody UpdatePasswordRequest request) {
        // TODO: implement password update logic
        throw new UnsupportedOperationException("Not implemented yet");
    }
}