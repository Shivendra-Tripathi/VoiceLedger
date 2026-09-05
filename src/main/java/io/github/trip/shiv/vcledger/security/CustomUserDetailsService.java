package io.github.trip.shiv.vcledger.security;


import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Bridges your application's User entity to Spring Security's UserDetails
 * contract. This is the single place where "email" is established as the
 * login identifier — everything downstream (AuthenticationManager, JWT
 * subject, /me endpoint) relies on this mapping being consistent.
 *
 * NOTE / ASSUMPTION: since your actual User entity wasn't provided, this
 * assumes it exposes getEmail() and getPassword(). If your entity uses
 * different accessor names, update the three lines inside loadUserByUsername
 * accordingly — nothing else in the auth stack needs to change.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + email));

        // org.springframework.security.core.userdetails.User is a ready-made
        // UserDetails implementation — no need to make your own User entity
        // implement UserDetails directly, which would otherwise couple your
        // JPA entity to a Spring Security interface.
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword()) // already BCrypt-hashed in the DB
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}