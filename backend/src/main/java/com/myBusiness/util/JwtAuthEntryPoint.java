package com.myBusiness.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JwtAuthEntryPoint is responsible for handling unauthorized access attempts.
 * When an unauthenticated request is made to a protected endpoint, this class sends an HTTP 401 error.
 */
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthEntryPoint.class);

    /**
     * Called whenever an exception is thrown due to an unauthenticated user trying to access a secured resource.
     * Sends a 401 Unauthorized error response and logs the unauthorized access attempt.
     *
     * @param request       The HttpServletRequest.
     * @param response      The HttpServletResponse.
     * @param authException The exception which caused the invocation.
     * @throws IOException If an input or output exception occurs.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        logger.warn("Unauthorized access attempt to {}. Error: {}", request.getRequestURI(), authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Acceso denegado. No autorizado.");
    }
}
