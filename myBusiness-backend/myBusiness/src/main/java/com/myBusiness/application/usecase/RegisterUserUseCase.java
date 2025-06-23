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
     * 1) Verifica si ese email ya existe o tiene un token pendiente.
     * 100% dentro de una transacción para consistencia.
     */
    @Transactional
    public void checkIfEmailPending(String email) {
        String normalized = email.trim().toLowerCase();

        if (userRepo.findByEmail(normalized).isPresent()) {
            throw new InvalidUserException("Email ya registrado: " + normalized);
        }

        boolean existsTokenPendiente = tokenRepo
            .findByPendingEmailAndUsedFalse(normalized)
            .isPresent();

        if (existsTokenPendiente) {
            throw new InvalidUserException("Ya hay un registro pendiente para ese email.");
        }
    }

    /**
     * 2) Crea un nuevo token con los datos “pendientes”.
     */
    @Transactional
    public String createPendingUser(UserRegisterDto dto) {
        String normalizedEmail = dto.getEmail().trim().toLowerCase();

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
        return Base64.getUrlEncoder()
                     .withoutPadding()
                     .encodeToString(random)
                     .substring(0, 8);
    }
}
