package io.github.trip.shiv.vcledger.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Password hashing mechanism.
     *
     * Passwords should NEVER be stored as plain text.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication provider responsible for:
     *
     * 1. Loading the user through UserDetailsService.
     * 2. Comparing the supplied password with the
     *    stored password using PasswordEncoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    /**
     * AuthenticationManager used by AuthService during login.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    /**
     * Main Spring Security configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

    	 http
         .csrf(csrf -> csrf.disable())

         .sessionManagement(session ->
             session.sessionCreationPolicy(
                 SessionCreationPolicy.STATELESS
             )
         )

         .authorizeHttpRequests(auth -> auth
        		 .requestMatchers("/api/auth/**", "/error","/api/test/public").permitAll()
             .anyRequest().authenticated()
         )

         .authenticationProvider(authenticationProvider())

         .addFilterBefore(
             jwtAuthenticationFilter,
             UsernamePasswordAuthenticationFilter.class
         );

     return http.build();
    	
    	
//    	 return http
//    	            .csrf(csrf -> csrf.disable())
//    	            .authorizeHttpRequests(auth -> auth
//    	                    .anyRequest().permitAll()
//    	            )
//    	            .build();
    }
}

