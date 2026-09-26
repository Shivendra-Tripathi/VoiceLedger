package io.github.trip.shiv.vcledger.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.trip.shiv.vcledger.core.dtos.authcontroller.req.LoginRequest;
import io.github.trip.shiv.vcledger.core.dtos.authcontroller.req.RegisterRequest;
import io.github.trip.shiv.vcledger.core.dtos.authcontroller.req.UpdatePasswordRequest;
import io.github.trip.shiv.vcledger.core.dtos.authcontroller.res.LoginResponse;
import io.github.trip.shiv.vcledger.core.dtos.authcontroller.res.RegisterResponse;
import io.github.trip.shiv.vcledger.core.utilities.SecurityUtils;
import io.github.trip.shiv.vcledger.entity.User;
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
    	
    
        RegisterResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    /**
     * Authenticate a user and issue access/refresh tokens.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
    	
    	
    	return ResponseEntity
    			.status(HttpStatus.ACCEPTED)
    			.body(authService.login(request));
    }

   

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