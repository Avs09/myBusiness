package com.myBusiness.service.impl;

import com.myBusiness.dto.AuthResponse;
import com.myBusiness.dto.LoginRequest;
import com.myBusiness.dto.RegisterRequest;
import com.myBusiness.dto.UpdateUserRequest;
import com.myBusiness.model.RefreshToken;
import com.myBusiness.model.Role;
import com.myBusiness.model.User;
import com.myBusiness.repository.RefreshTokenRepository;
import com.myBusiness.repository.RoleRepository;
import com.myBusiness.repository.UserRepository;
import com.myBusiness.service.AuthService;
import com.myBusiness.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Intento de registro con email ya existente: {}", request.getEmail());
            throw new IllegalArgumentException("El email ya está en uso.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("Rol de usuario no encontrado."));
        user.setRoles(Collections.singleton(userRole));

        userRepository.save(user);
        logger.info("Nuevo usuario registrado: {}", request.getEmail());
    }

    @Override
    public AuthResponse authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Intento de inicio de sesión con email no registrado: {}", request.getEmail());
                    return new IllegalArgumentException("Credenciales inválidas.");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Contraseña incorrecta para el usuario: {}", request.getEmail());
            throw new IllegalArgumentException("Credenciales inválidas.");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        String refreshToken = generateRefreshToken(user);
        logger.info("Usuario autenticado exitosamente: {}", request.getEmail());
        return new AuthResponse(token, refreshToken);
    }

    @Override
    public void updateUser(Long id, UpdateUserRequest request, String currentUserRole) {
        if (!currentUserRole.equals("ADMIN")) {
            logger.warn("Acceso denegado: usuario sin permisos intenta actualizar usuario con ID: {}", id);
            throw new AccessDeniedException("No tienes permiso para realizar esta acción.");
        }

        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));

        user.setEmail(request.getEmail());
        // Actualizar roles si se proporcionan
        if (request.getRoles() != null) {
            List<Role> roleList = roleRepository.findAllById(request.getRoles());
            Set<Role> roles = new HashSet<>(roleList); // Convertir la lista en un conjunto
            user.setRoles(roles);
        }


        userRepository.save(user);
        logger.info("Usuario actualizado: ID {}, nuevo email: {}", id, request.getEmail());
    }

    @Override
    public void deleteUser(Long id, String currentUserRole) {
        if (!currentUserRole.equals("ADMIN")) {
            logger.warn("Acceso denegado: usuario sin permisos intenta eliminar usuario con ID: {}", id);
            throw new AccessDeniedException("No tienes permiso para realizar esta acción.");
        }

        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));

        userRepository.delete(user);
        logger.info("Usuario eliminado: ID {}", id);
    }

    public String generateRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(jwtUtil.generateToken(user.getEmail()));
        refreshToken.setExpiryDate(Instant.now().plusMillis(7 * 24 * 60 * 60 * 1000)); // 7 días

        refreshTokenRepository.save(refreshToken);
        logger.info("Refresh token generado para el usuario: {}", user.getEmail());
        return refreshToken.getToken();
    }

    public String renewAccessToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> {
                    logger.warn("Intento de renovación con refresh token inválido: {}", refreshToken);
                    return new IllegalArgumentException("Refresh token inválido o expirado.");
                });

        if (storedToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(storedToken);
            logger.warn("Refresh token expirado eliminado: {}", refreshToken);
            throw new IllegalArgumentException("Refresh token expirado.");
        }

        User user = storedToken.getUser();
        logger.info("Access token renovado para el usuario: {}", user.getEmail());
        return jwtUtil.generateToken(user.getEmail());
    }
}
