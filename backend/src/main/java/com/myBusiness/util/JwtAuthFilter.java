package com.myBusiness.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.myBusiness.service.impl.UserDetailsServiceImpl;
import java.io.IOException;

/**
 * JwtAuthFilter intercepts incoming HTTP requests to extract and validate JWT tokens.
 * If a valid token is found, it sets the corresponding authentication in the SecurityContext.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Constructor for dependency injection.
     *
     * @param jwtUtil            Utility class for JWT operations.
     * @param userDetailsService Service to load user details.
     */
    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filters each HTTP request to check for a valid JWT token in the "Authorization" header.
     * If a token is present and valid, the user authentication is set in the SecurityContext.
     *
     * @param request  The HTTP request.
     * @param response The HTTP response.
     * @param chain    The filter chain.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException      If an input or output exception occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            try {
                String username = jwtUtil.extractUsername(jwt);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        logger.debug("JWT token validated successfully for user: {}", username);
                    } else {
                        logger.warn("JWT token validation failed for user: {}", username);
                    }
                }
            } catch (Exception e) {
                logger.error("Error processing JWT token: {}", e.getMessage(), e);
            }
        } else {
            logger.debug("No JWT token found in request header for URI: {}", request.getRequestURI());
        }
        chain.doFilter(request, response);
    }
}
