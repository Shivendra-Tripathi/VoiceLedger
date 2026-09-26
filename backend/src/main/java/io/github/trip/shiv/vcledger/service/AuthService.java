package io.github.trip.shiv.vcledger.service;


import io.github.trip.shiv.vcledger.core.dtos.authcontroller.res.RegisterResponse;
import io.github.trip.shiv.vcledger.core.exceptions.custom.business.EmailAlreadyExistsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.github.trip.shiv.vcledger.core.dtos.authcontroller.req.LoginRequest;
import io.github.trip.shiv.vcledger.core.dtos.authcontroller.req.RegisterRequest;
import io.github.trip.shiv.vcledger.core.dtos.authcontroller.res.LoginResponse;
import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.security.JwtService;

@Service
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserService userService,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new user.
     */
    public RegisterResponse register(RegisterRequest request) {
        // Check whether email is already registered
        if (userService.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        User user = userService.createUser(request.getUsername(), request.getEmail(), request.getPassword(),null);
        return new RegisterResponse(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
    }

    /**
     * Authenticates a user and generates a JWT.
     */
    public LoginResponse login(LoginRequest request) {

        /*
         * AuthenticationManager:
         *
         * 1. Finds the user using the email.
         * 2. Gets the stored password.
         * 3. Uses PasswordEncoder to compare passwords.
         * 4. Throws an AuthenticationException if authentication fails.
         */
    	Authentication authentication =
    	        authenticationManager.authenticate(
    	                new UsernamePasswordAuthenticationToken(
    	                        request.getEmail(),
    	                        request.getPassword()
    	                )
    	        );

    	/*
    	 * Authentication succeeded.
    	 *
    	 * Load the user so that we can generate the JWT.
    	 */
    	UserDetails userDetails =
    	        (UserDetails) authentication.getPrincipal();

    	String token = jwtService.generateToken(userDetails);

    	return new LoginResponse(token,"Bearer");
    }
}

