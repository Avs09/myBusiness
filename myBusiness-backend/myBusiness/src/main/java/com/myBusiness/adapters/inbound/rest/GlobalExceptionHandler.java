package com.myBusiness.adapters.inbound.rest;

import com.myBusiness.application.exception.CategoryNotFoundException;
import com.myBusiness.application.exception.ProductNotFoundException;
import com.myBusiness.application.exception.UnitNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1) Captura violaciones de @Valid en los DTOs → 400 BAD_REQUEST.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .toList();
        return build(HttpStatus.BAD_REQUEST, String.join("; ", errors));
    }

    /**
     * 2) “No encontrado” para categorías, unidades o productos → 404 NOT_FOUND.
     */
    @ExceptionHandler({
        CategoryNotFoundException.class,
        UnitNotFoundException.class,
        ProductNotFoundException.class
    })
    public ResponseEntity<Map<String,Object>> handleNotFound(RuntimeException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * 3) Cualquier otra excepción → 500 INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleAll(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private ResponseEntity<Map<String,Object>> build(HttpStatus status, String msg) {
        Map<String,Object> body = Map.of(
            "timestamp", Instant.now(),
            "status",    status.value(),
            "error",     status.getReasonPhrase(),
            "message",   msg
        );
        return ResponseEntity.status(status).body(body);
    }
}
