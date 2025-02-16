package com.myBusiness.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myBusiness.dto.AuthResponse;
import com.myBusiness.dto.UpdateUserRequest;
import com.myBusiness.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> renewAccessToken(@RequestBody String refreshToken) {
        String newAccessToken = authService.renewAccessToken(refreshToken);
        return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
    }


    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request, @RequestHeader("Role") String currentUserRole) {
        authService.updateUser(id, request, currentUserRole);
        return ResponseEntity.ok("Usuario actualizado con éxito.");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, @RequestHeader("Role") String currentUserRole) {
        authService.deleteUser(id, currentUserRole);
        return ResponseEntity.ok("Usuario eliminado con éxito.");
    }
}
