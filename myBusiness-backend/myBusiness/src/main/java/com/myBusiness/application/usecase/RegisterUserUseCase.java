// src/main/java/com/myBusiness/application/usecase/RegisterUserUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.UserRegisterDto;
import com.myBusiness.application.exception.InvalidUserException;
import com.myBusiness.domain.model.VerificationToken;
import com.myBusiness.domain.port.UserRepository;
import com.myBusiness.domain.port.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepo;                     
    private final VerificationTokenRepository tokenRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 1) Verifica si ese email ya existe en la tabla 'users'. Si existe, arroja InvalidUserException YA.
     * 2) Si no existe en users, revisa si hay un token pendiente (used=false) para ese mismo correo.
     *    Si hay uno, arroja InvalidUserException("Ya hay un registro pendiente para ese email.").
     */
    public void checkIfEmailPending(String email) {
        String normalized = email.trim().toLowerCase();

        // 1.1) Comprueba en la tabla users:
        if (userRepo.findByEmail(normalized).isPresent()) {
            throw new InvalidUserException("Email ya registrado: " + normalized);
        }

        // 1.2) Comprueba si hay token pendiente para ese mismo correo:
        boolean existsTokenPendiente = tokenRepo
            .findByPendingEmailAndUsedFalse(normalized)
            .isPresent();

        if (existsTokenPendiente) {
            throw new InvalidUserException("Ya hay un registro pendiente para ese email.");
        }
    }

    /**
     * 2) Crea un nuevo token con nombre, email y contraseña (hasheada) pendientes. NO guarda usuario.
     */
    @Transactional
    public String createPendingUser(UserRegisterDto dto) {
        String normalizedEmail = dto.getEmail().trim().toLowerCase();

        // Generar código alfanumérico de 8 caracteres
        String code = generateRandomCode();

        VerificationToken vt = VerificationToken.builder()
            .token(code)
            .createdAt(Instant.now())
            .used(false)
            .pendingEmail(normalizedEmail)
            .pendingName(dto.getName().trim())
            .pendingPasswordHash(passwordEncoder.encode(dto.getPassword()))
            .build();

        tokenRepo.save(vt);
        return code;
    }

    private String generateRandomCode() {
        byte[] random = new byte[24];
        new SecureRandom().nextBytes(random);
        // Base64 URL-safe (sin “+”, “/”, ni “=”), y tomamos los primeros 8 caracteres
        return Base64.getUrlEncoder()
                     .withoutPadding()
                     .encodeToString(random)
                     .substring(0, 8);
    }
}
