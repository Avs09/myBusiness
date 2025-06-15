package com.myBusiness.adapters.outbound.persistence;

import com.myBusiness.domain.model.VerificationToken;
import com.myBusiness.domain.port.VerificationTokenRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class VerificationTokenRepositoryImpl implements VerificationTokenRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public VerificationToken save(VerificationToken token) {
        if (token.getId() == null) {
            em.persist(token);
            return token;
        } else {
            return em.merge(token);
        }
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        TypedQuery<VerificationToken> q = em.createQuery(
            "SELECT vt FROM VerificationToken vt WHERE vt.token = :tok",
            VerificationToken.class);
        q.setParameter("tok", token);
        return q.getResultStream().findFirst();
    }

    @Override
    public Optional<VerificationToken> findByPendingEmail(String pendingEmail) {
        TypedQuery<VerificationToken> q = em.createQuery(
            "SELECT vt FROM VerificationToken vt " +
            " WHERE vt.pendingEmail = :email " +
            " ORDER BY vt.createdAt DESC",
            VerificationToken.class);
        q.setParameter("email", pendingEmail);
        return q.setMaxResults(1).getResultStream().findFirst();
    }

    @Override
    public Optional<VerificationToken> findByPendingEmailAndUsedFalse(String pendingEmail) {
        TypedQuery<VerificationToken> q = em.createQuery(
            "SELECT vt FROM VerificationToken vt " +
            " WHERE vt.pendingEmail = :email " +
            "   AND vt.used = false " +
            " ORDER BY vt.createdAt DESC",
            VerificationToken.class);
        q.setParameter("email", pendingEmail);
        return q.setMaxResults(1).getResultStream().findFirst();
    }

    @Override
    public Optional<VerificationToken> findByTokenAndPendingEmailAndUsedFalse(
            String token,
            String pendingEmail) {

        TypedQuery<VerificationToken> q = em.createQuery(
            "SELECT vt FROM VerificationToken vt " +
            " WHERE vt.token = :tok " +
            "   AND vt.pendingEmail = :email " +
            "   AND vt.used = false",
            VerificationToken.class);
        q.setParameter("tok", token);
        q.setParameter("email", pendingEmail);
        return q.getResultStream().findFirst();
    }
}
