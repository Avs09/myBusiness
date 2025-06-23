package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.VerifyCodeDto;
import com.myBusiness.application.exception.InvalidCodeException;
import com.myBusiness.application.exception.TokenExpiredException;
import com.myBusiness.domain.model.User;
import com.myBusiness.domain.model.VerificationToken;
import com.myBusiness.domain.port.UserRepository;
import com.myBusiness.domain.port.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ConfirmUserUseCase {

    private final VerificationTokenRepository tokenRepo;
    private final UserRepository userRepo;

    /**
     * Verifica el token, crea el usuario, marca el token como usado.
     * Todo dentro de la misma transacción para consistencia.
     */
    @Transactional
    public void execute(VerifyCodeDto dto) {
        String email = dto.getEmail().trim().toLowerCase();
        String code = dto.getCode().trim();

        VerificationToken vt = tokenRepo.findByToken(code)
            .orElseThrow(() -> new InvalidCodeException("Código de verificación no válido"));

        if (!vt.getPendingEmail().equals(email)) {
            throw new InvalidCodeException("El código no coincide con el email proporcionado");
        }

        if (vt.isUsed()) {
            throw new InvalidCodeException("Este código ya fue usado");
        }

        Instant now = Instant.now();
        if (Duration.between(vt.getCreatedAt(), now).toMinutes() > 15) {
            throw new TokenExpiredException("El código ha expirado");
        }

        User u = User.builder()
            .username(vt.getPendingEmail())
            .name(vt.getPendingName())
            .password(vt.getPendingPasswordHash())
            .enabled(true)
            .build();

        userRepo.save(u);

        vt.setUsed(true);
        vt.setUser(u);
        tokenRepo.save(vt);
    }
}
