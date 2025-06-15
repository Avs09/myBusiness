package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.EmailDto;
import com.myBusiness.application.exception.InvalidUserException;
import com.myBusiness.domain.model.VerificationToken;
import com.myBusiness.domain.port.VerificationTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SendVerificationCodeUseCase {

    private final VerificationTokenRepository tokenRepo;
    private final JavaMailSender mailSender;

    /**
     * Envía por SMTP el código que ya existe en verification_tokens (used = false) para un email.
     * 1) Busca el token más reciente no usado para pendingEmail.
     * 2) Si no existe, lanza InvalidUserException.
     * 3) Envía un SimpleMailMessage con el código.
     */
    
    @Async
    @Transactional
    public void execute(EmailDto dto) {
        String normalizedEmail = dto.getEmail().trim().toLowerCase();

        Optional<VerificationToken> maybeToken =
            tokenRepo.findByPendingEmailAndUsedFalse(normalizedEmail);

        if (maybeToken.isEmpty()) {
            throw new InvalidUserException("No existe un token pendiente para ese email.");
        }

        VerificationToken vt = maybeToken.get();
        String code = vt.getToken();

        // Armar y enviar el correo
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(normalizedEmail);
        msg.setSubject("Tu código de verificación");
        msg.setText("Aquí está tu código de verificación: " + code);
     

        mailSender.send(msg);
    }
}
