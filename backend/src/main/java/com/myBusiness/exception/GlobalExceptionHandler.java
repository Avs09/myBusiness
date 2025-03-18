package com.myBusiness.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler centralizes the handling of exceptions thrown by controllers.
 * This ensures that error responses are uniform and informative across the entire application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Constructs a standardized error response.
     *
     * @param status  The HTTP status for the error.
     * @param message The error message.
     * @param request The current web request.
     * @return A map representing the error response.
     */
    private Map<String, Object> createErrorResponse(HttpStatus status, String message, WebRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));
        return errorResponse;
    }

    /**
     * Handles IllegalArgumentException exceptions, returning a 400 Bad Request response.
     *
     * @param ex      The thrown IllegalArgumentException.
     * @param request The current web request.
     * @return A ResponseEntity containing the error response.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        logger.error("IllegalArgumentException encountered: {}", ex.getMessage(), ex);
        Map<String, Object> errorResponse = createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles EntityNotFoundException exceptions, returning a 404 Not Found response.
     *
     * @param ex      The thrown EntityNotFoundException.
     * @param request The current web request.
     * @return A ResponseEntity containing the error response.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        logger.error("EntityNotFoundException encountered: {}", ex.getMessage(), ex);
        Map<String, Object> errorResponse = createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles AccessDeniedException exceptions, returning a 403 Forbidden response.
     *
     * @param ex      The thrown AccessDeniedException.
     * @param request The current web request.
     * @return A ResponseEntity containing the error response.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        logger.error("AccessDeniedException encountered: {}", ex.getMessage(), ex);
        Map<String, Object> errorResponse = createErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles optimistic locking failures, returning a 409 Conflict response.
     *
     * @param ex      The ObjectOptimisticLockingFailureException.
     * @param request The current web request.
     * @return A ResponseEntity containing the error response.
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<?> handleOptimisticLockingFailure(ObjectOptimisticLockingFailureException ex, WebRequest request) {
        logger.error("Optimistic locking failure: {}", ex.getMessage(), ex);
        Map<String, Object> errorResponse = createErrorResponse(HttpStatus.CONFLICT, "Conflict detected: " + ex.getMessage(), request);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handles all other exceptions not explicitly handled by other methods,
     * returning a 500 Internal Server Error response.
     *
     * @param ex      The thrown exception.
     * @param request The current web request.
     * @return A ResponseEntity containing the error response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Unhandled exception: {}", ex.getMessage(), ex);
        Map<String, Object> errorResponse = createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
