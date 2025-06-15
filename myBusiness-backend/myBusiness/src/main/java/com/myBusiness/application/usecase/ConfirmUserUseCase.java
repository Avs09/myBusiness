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

    @Transactional
    public void execute(VerifyCodeDto dto) {
        String email = dto.getEmail().trim().toLowerCase();
        String code = dto.getCode().trim();

        // 1) Buscar token pendiente:
        VerificationToken vt = tokenRepo.findByToken(code)
            .orElseThrow(() -> new InvalidCodeException("Código de verificación no válido"));

        // 2) Verificar que coincide con el email “pendingEmail”:
        if (!vt.getPendingEmail().equals(email)) {
            throw new InvalidCodeException("El código no coincide con el email proporcionado");
        }

        // 3) Verificar que no esté usado y que no haya expirado (ej: 15 minutos):
        if (vt.isUsed()) {
            throw new InvalidCodeException("Este código ya fue usado");
        }
        Instant now = Instant.now();
        if (Duration.between(vt.getCreatedAt(), now).toMinutes() > 15) {
            throw new TokenExpiredException("El código ha expirado");
        }

        // 4) Crear el User real usando los campos “pending…” guardados en el token:
        User u = User.builder()
            .username(vt.getPendingEmail())
            .name(vt.getPendingName())
            // El campo pendingPasswordHash ya está hasheado, así que asignamos directamente:
            .password(vt.getPendingPasswordHash())
            .enabled(true)
            .build();
        userRepo.save(u);

        // 5) Marcar token como usado y asociar al User:
        vt.setUsed(true);
        vt.setUser(u);
        tokenRepo.save(vt);
    }
}
