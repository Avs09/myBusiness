package com.myBusiness.util;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

/**
 * JwtUtil provides utility methods for generating and validating JSON Web Tokens (JWT).
 * It handles token creation, extraction of claims, and token validation.
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    // Secret key used for signing the JWT. Loaded from application properties.
    @Value("${jwt.secret}")
    private String secretKey;

    // Token expiration time in milliseconds. Loaded from application properties.
    @Value("${jwt.expiration}")
    private Long jwtExpirationMs;

    /**
     * Generates a JWT token for the given username.
     *
     * @param username the username for which the token is generated.
     * @return a signed JWT token.
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    /**
     * Extracts the username (subject) from the given JWT token.
     *
     * @param token the JWT token.
     * @return the username contained in the token.
     */
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            logger.error("Failed to extract username from token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Extracts a specific claim from the token using the provided resolver function.
     *
     * @param token          the JWT token.
     * @param claimsResolver a function to resolve the claim.
     * @param <T>            the type of the claim.
     * @return the extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims present in the token.
     *
     * @param token the JWT token.
     * @return the claims contained in the token.
     * @throws JwtException if the token is invalid or expired.
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token expired: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            logger.error("JWT token invalid: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Validates the JWT token against the given user details.
     *
     * @param token       the JWT token.
     * @param userDetails the user details to validate against.
     * @return true if the token is valid; false otherwise.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isExpired = isTokenExpired(token);
            if (isExpired) {
                logger.warn("JWT token is expired");
            }
            return username.equals(userDetails.getUsername()) && !isExpired;
        } catch (JwtException e) {
            logger.error("JWT token validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the token is expired.
     *
     * @param token the JWT token.
     * @return true if the token is expired; false otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the token.
     *
     * @param token the JWT token.
     * @return the expiration date.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
