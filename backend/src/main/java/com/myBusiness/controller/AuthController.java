package com.myBusiness.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.myBusiness.dto.AuthResponse;
import com.myBusiness.dto.LoginRequest;
import com.myBusiness.dto.RegisterRequest;
import com.myBusiness.dto.UpdateUserRequest;
import com.myBusiness.service.AuthService;

import jakarta.validation.Valid;

/**
 * AuthController expone endpoints REST para operaciones de autenticación.
 * Incluye login, registro, renovación de token, actualización y eliminación de usuario, y logout.
 */
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    /**
     * Constructor para inyección de dependencias.
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * Endpoint para autenticar al usuario y obtener un token JWT.
     * 
     * @param loginRequest Payload con email y contraseña.
     * @return Respuesta con token de acceso y refresh token.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.authenticate(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint para registrar un nuevo usuario.
     * 
     * @param registerRequest Payload de registro.
     * @return Mensaje de confirmación con código 201 CREATED.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente.");
    }
    
    /**
     * Endpoint para renovar el token de acceso usando un refresh token.
     * 
     * @param refreshToken Refresh token a intercambiar.
     * @return Respuesta con el nuevo token de acceso y el refresh token.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> renewAccessToken(@RequestBody String refreshToken) {
        String newAccessToken = authService.renewAccessToken(refreshToken);
        return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
    }
    
    /**
     * Endpoint para cerrar sesión invalidando el refresh token.
     * 
     * @param refreshToken Refresh token a invalidar.
     * @return Mensaje de confirmación.
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.ok("Logout exitoso.");
    }
    
    /**
     * Endpoint para actualizar los detalles de un usuario.
     * Requiere un rol de usuario válido para autorizar la actualización.
     * 
     * @param id ID del usuario a actualizar.
     * @param request Payload con los datos a actualizar.
     * @param currentUserRole Rol del usuario actual que realiza la actualización.
     * @return Mensaje de confirmación.
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, 
                                             @Valid @RequestBody UpdateUserRequest request, 
                                             @RequestHeader("Role") String currentUserRole) {
        authService.updateUser(id, request, currentUserRole);
        return ResponseEntity.ok("Usuario actualizado con éxito.");
    }

    /**
     * Endpoint para eliminar un usuario.
     * Requiere un rol de usuario válido para autorizar la eliminación.
     * 
     * @param id ID del usuario a eliminar.
     * @param currentUserRole Rol del usuario actual que realiza la eliminación.
     * @return Mensaje de confirmación.
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, 
                                             @RequestHeader("Role") String currentUserRole) {
        authService.deleteUser(id, currentUserRole);
        return ResponseEntity.ok("Usuario eliminado con éxito.");
    }
}
