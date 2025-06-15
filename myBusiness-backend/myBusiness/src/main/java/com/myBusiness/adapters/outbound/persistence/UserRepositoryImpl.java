package com.myBusiness.adapters.outbound.persistence;

import com.myBusiness.domain.model.User;
import com.myBusiness.domain.port.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            em.persist(user);
            return user;
        } else {
            return em.merge(user);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        TypedQuery<User> q = em.createQuery(
            "SELECT u FROM User u WHERE u.username = :email",
            User.class);
        q.setParameter("email", email);
        
        var results = q.getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(results.get(0));
    }
}
