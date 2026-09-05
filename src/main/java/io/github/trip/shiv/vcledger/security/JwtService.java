
package io.github.trip.shiv.vcledger.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

/**
 * Responsible for everything related to JWT lifecycle:
 * generating tokens, parsing claims out of them, and validating them.
 *
 * This class deliberately knows nothing about HTTP, Spring Security's
 * SecurityContext, or the persistence layer.
 */
@Service
public class JwtService {

    /**
     * Base64-encoded signing secret, injected from configuration.
     *
     * IMPORTANT:
     * This must be a stable, externally supplied secret.
     * If this value changes, previously issued JWTs become invalid.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Token validity in milliseconds.
     */
    @Value("${jwt.expiration}")
    private long expirationMs;

    /**
     * Creates the HMAC signing key from the configured Base64 secret.
     *
     * For HS256, the key must be at least 256 bits (32 bytes).
     */
    private SecretKey signingKey() {

        byte[] keyBytes = Base64.getDecoder().decode(secret);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a signed JWT for the given user.
     *
     * Subject = user's email.
     */
    public String generateToken(UserDetails userDetails) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey())
                .compact();
    }

    /**
     * Extracts the username/email from the JWT subject.
     */
    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the JWT.
     */
    public Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic method for extracting any claim from the JWT.
     */
    private <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    /**
     * Parses and verifies the JWT.
     *
     * Modern JJWT 0.12.x API:
     *
     * parser()
     *     .verifyWith(...)
     *     .build()
     *     .parseSignedClaims(...)
     *     .getPayload()
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Validates that:
     *
     * 1. Token subject matches the user's username/email.
     * 2. Token has not expired.
     * 3. Token signature is valid.
     * 4. Token is structurally valid.
     */
    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        try {

            String username = extractUsername(token);

            return username.equals(userDetails.getUsername())
                    && !isTokenExpired(token);

        } catch (JwtException | IllegalArgumentException e) {

            return false;
        }
    }

    /**
     * Checks whether the JWT has expired.
     */
    private boolean isTokenExpired(String token) {

        try {

            return extractExpiration(token).before(new Date());

        } catch (ExpiredJwtException e) {

            return true;
        }
    }
}

