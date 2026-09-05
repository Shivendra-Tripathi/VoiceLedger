package io.github.trip.shiv.vcledger.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
	
	
    // Public endpoint
    @GetMapping("/public")
    public String publicEndpoint() {
        return "Public endpoint is working";
    }

    // Protected endpoint
    @GetMapping("/protected")
    public String protectedEndpoint(Authentication authentication) {

        return "JWT authentication successful. Logged in as: "
                + authentication.getName();
    }
}