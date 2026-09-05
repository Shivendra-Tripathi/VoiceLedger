package io.github.trip.shiv.vcledger.security;


import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

/**
 * Runs once per request, before UsernamePasswordAuthenticationFilter.
 *
 * Responsibility: if a valid Bearer token is present, populate the
 * SecurityContext with an Authentication object so that downstream
 * authorization checks (and @AuthenticationPrincipal / SecurityContext
 * lookups in controllers) see an authenticated user.
 *
 * Deliberately permissive on failure: this filter never throws or blocks
 * the chain on a missing/invalid/expired token. It just leaves the
 * SecurityContext empty, and the SecurityFilterChain's authorization
 * rules (see SecurityConfig) are what ultimately reject the request with
 * 401/403 further down the chain. This keeps the filter simple and avoids
 * accidentally breaking public endpoints (register/login) which also pass
 * through this filter but carry no token.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader(AUTH_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            // No token, or not a Bearer token — nothing for us to do.
            // Let the request continue; SecurityConfig decides if it's allowed.
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(BEARER_PREFIX.length());

        try {
            final String email = jwtService.extractUsername(jwt);

            // Only attempt authentication if there isn't already one in the
            // context (avoids redundant work if something upstream set it).
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // credentials not needed post-authentication
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
                // If not valid (expired/signature mismatch/user mismatch),
                // we simply don't authenticate — no exception thrown here.
            }
        } catch (Exception ex) {
            // Malformed token, unknown user, parsing failure, etc.
            // Swallow and leave the SecurityContext unauthenticated;
            // do NOT let this exception break the filter chain.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}