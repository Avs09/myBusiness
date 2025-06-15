// src/main/java/com/myBusiness/adapters/inbound/rest/AuthenticationController.java
package com.myBusiness.adapters.inbound.rest;

import com.myBusiness.domain.port.UserRepository;          
import com.myBusiness.infrastructure.security.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepo;  // ← Inyectamos UserRepository para verificar existencia de email

    public record AuthRequest(
        @NotBlank @Email(message = "Email inválido") String email,
        @NotBlank(message = "Password obligatoria") String password
    ) {}

    public record AuthResponse(String token) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest req) {
        String email = req.email().trim().toLowerCase();
        String password = req.password();

        // 1) Verificar si el email existe en la BD
        if (userRepo.findByEmail(email).isEmpty()) {
            Map<String,Object> body = Map.of(
                "timestamp", Instant.now(),
                "status", HttpStatus.UNAUTHORIZED.value(),
                "error", HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "message", "Email no registrado"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }

        try {
            // 2) Autenticamos; si falla, BadCredentialsException ó DisabledException
            var authToken = new UsernamePasswordAuthenticationToken(email, password);
            authManager.authenticate(authToken);

            // 3) Cargar detalles y generar JWT
            UserDetails user = userDetailsService.loadUserByUsername(email);
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));

        } catch (DisabledException ex) {
            // Cuenta existe pero no está habilitada
            Map<String,Object> body = Map.of(
                "timestamp", Instant.now(),
                "status", HttpStatus.FORBIDDEN.value(),
                "error", HttpStatus.FORBIDDEN.getReasonPhrase(),
                "message", "Cuenta no verificada"
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);

        } catch (BadCredentialsException ex) {
            // La contraseña es incorrecta (email sí existe)
            Map<String,Object> body = Map.of(
                "timestamp", Instant.now(),
                "status", HttpStatus.UNAUTHORIZED.value(),
                "error", HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "message", "Contraseña incorrecta"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }
    }
}
