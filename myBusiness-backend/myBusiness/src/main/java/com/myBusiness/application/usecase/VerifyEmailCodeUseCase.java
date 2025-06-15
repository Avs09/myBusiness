// src/main/java/com/myBusiness/application/usecase/VerifyEmailCodeUseCase.java

package com.myBusiness.application.usecase;

import com.myBusiness.application.exception.InvalidCodeException;
import com.myBusiness.application.exception.TokenExpiredException;
import com.myBusiness.domain.model.User;
import com.myBusiness.domain.model.VerificationToken;
import com.myBusiness.domain.port.UserRepository;
import com.myBusiness.domain.port.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class VerifyEmailCodeUseCase {

    private final UserRepository userRepo;
    private final VerificationTokenRepository tokenRepo;

    @Transactional
    public void execute(String email, String code) {
        String normalizedEmail = email.trim().toLowerCase();
        String normalizedCode = code.trim();

        // 1) Buscar token activo (used=false) que coincida con código + pendingEmail
        VerificationToken vt = tokenRepo
            .findByTokenAndPendingEmailAndUsedFalse(normalizedCode, normalizedEmail)
            .orElseThrow(() -> new InvalidCodeException("Código de verificación no válido"));

        // 2) Validar expiración (15 minutos)
        Instant now = Instant.now();
        if (Duration.between(vt.getCreatedAt(), now).toMinutes() > 15) {
            throw new TokenExpiredException("El código ha expirado");
        }

        // 3) Crear usuario definitivo en tabla users
        User u = User.builder()
            .username(vt.getPendingEmail())
            .name(vt.getPendingName())
            .password(vt.getPendingPasswordHash())
            .enabled(true)
            .build();
        User savedUser = userRepo.save(u);

        // 4) Marcar token como used=true y enlazar con el usuario creado
        vt.setUsed(true);
        vt.setUser(savedUser);
        tokenRepo.save(vt);
    }
}
