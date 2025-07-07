package com.myBusiness.adapters.outbound.persistence;

import com.myBusiness.domain.model.Notification;
import com.myBusiness.domain.port.NotificationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            em.persist(notification);
            return notification;
        } else {
            return em.merge(notification);
        }
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return Optional.ofNullable(em.find(Notification.class, id));
    }

    @Override
    public List<Notification> findAllUnread() {
        TypedQuery<Notification> q = em.createQuery(
            "SELECT n FROM Notification n " +
            " WHERE n.isRead = false " +
            " ORDER BY n.createdDate DESC",
            Notification.class
        );
        return q.getResultList();
    }

    @Override
    public List<Notification> findAll() {
        TypedQuery<Notification> q = em.createQuery(
            "SELECT n FROM Notification n " +
            " ORDER BY n.createdDate DESC",
            Notification.class
        );
        return q.getResultList();
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        Notification n = em.find(Notification.class, id);
        if (n != null && !n.isRead()) {
            n.setRead(true);
            em.merge(n);
        }
    }
}
