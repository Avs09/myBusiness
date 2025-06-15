package com.myBusiness.domain.port;

import com.myBusiness.domain.model.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository {
    VerificationToken save(VerificationToken token);
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByPendingEmail(String pendingEmail);
    /**
     * Busca un token “pendiente” (used = false) para un mismo pendingEmail.
     */
    Optional<VerificationToken> findByPendingEmailAndUsedFalse(String pendingEmail);
    
    Optional<VerificationToken> findByTokenAndPendingEmailAndUsedFalse(
            String token,
            String pendingEmail
    );
}
