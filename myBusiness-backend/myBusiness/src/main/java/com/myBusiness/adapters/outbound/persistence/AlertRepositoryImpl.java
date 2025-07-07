package com.myBusiness.adapters.outbound.persistence;

import com.myBusiness.domain.model.Alert;
import com.myBusiness.domain.port.AlertRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class AlertRepositoryImpl implements AlertRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Alert save(Alert alert) {
        if (alert.getId() == null) {
            em.persist(alert);
            return alert;
        } else {
            return em.merge(alert);
        }
    }

    @Override
    public Optional<Alert> findById(Long id) {
        // JOIN FETCH producto y movimiento para inicializar
        TypedQuery<Alert> q = em.createQuery(
            "SELECT a FROM Alert a " +
            " LEFT JOIN FETCH a.product p " +
            " LEFT JOIN FETCH a.movement m " +
            " WHERE a.id = :id",
            Alert.class
        );
        q.setParameter("id", id);
        return q.getResultStream().findFirst();
    }

    @Override
    public List<Alert> findAllUnread() {
        TypedQuery<Alert> q = em.createQuery(
            "SELECT a FROM Alert a " +
            " JOIN FETCH a.product p " +
            " LEFT JOIN FETCH a.movement m " +
            " WHERE a.isRead = false " +
            " ORDER BY a.triggeredAt DESC",
            Alert.class
        );
        return q.getResultList();
    }

    @Override
    public List<Alert> findAllByProductId(Long productId) {
        TypedQuery<Alert> q = em.createQuery(
            "SELECT a FROM Alert a " +
            " JOIN FETCH a.product p " +
            " LEFT JOIN FETCH a.movement m " +
            " WHERE p.id = :pid " +
            " ORDER BY a.triggeredAt DESC",
            Alert.class
        );
        q.setParameter("pid", productId);
        return q.getResultList();
    }

    @Override
    public List<Alert> findAll() {
        TypedQuery<Alert> q = em.createQuery(
            "SELECT a FROM Alert a " +
            " JOIN FETCH a.product p " +
            " LEFT JOIN FETCH a.movement m " +
            " ORDER BY a.triggeredAt DESC",
            Alert.class
        );
        return q.getResultList();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        findById(id).ifPresent(em::remove);
    }
}
