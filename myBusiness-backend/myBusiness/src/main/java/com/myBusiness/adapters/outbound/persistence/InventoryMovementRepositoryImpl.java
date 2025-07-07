// src/main/java/com/myBusiness/adapters/outbound/persistence/InventoryMovementRepositoryImpl.java
package com.myBusiness.adapters.outbound.persistence;

import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.port.InventoryMovementRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Repository
public class InventoryMovementRepositoryImpl implements InventoryMovementRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public InventoryMovement save(InventoryMovement movement) {
        if (movement.getId() == null) {
            em.persist(movement);
            return movement;
        } else {
            return em.merge(movement);
        }
    }

    @Override
    public Optional<InventoryMovement> findById(Long id) {
        TypedQuery<InventoryMovement> q = em.createQuery(
            "SELECT m FROM InventoryMovement m " +
            " JOIN FETCH m.product p " +
            " JOIN FETCH p.category " +
            " JOIN FETCH p.unit " +
            " WHERE m.id = :id",
            InventoryMovement.class
        );
        q.setParameter("id", id);
        return q.getResultStream().findFirst();
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    @Override
    public List<InventoryMovement> findAllByProductId(Long productId) {
        if (productId == null) {
            return findAll();
        }
        TypedQuery<InventoryMovement> q = em.createQuery(
            "SELECT m FROM InventoryMovement m " +
            " JOIN FETCH m.product p " +
            " JOIN FETCH p.category " +
            " JOIN FETCH p.unit " +
            " WHERE p.id = :pid " +
            " ORDER BY m.movementDate DESC",
            InventoryMovement.class
        );
        q.setParameter("pid", productId);
        return q.getResultList();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        findById(id).ifPresent(em::remove);
    }

    @Override
    public List<InventoryMovement> findAll() {
        TypedQuery<InventoryMovement> q = em.createQuery(
            "SELECT m FROM InventoryMovement m " +
            " JOIN FETCH m.product p " +
            " JOIN FETCH p.category " +
            " JOIN FETCH p.unit " +
            " ORDER BY m.movementDate DESC",
            InventoryMovement.class
        );
        return q.getResultList();
    }

    @Override
    public List<InventoryMovement> findTopNByOrderByMovementDateDesc(int limit) {
        TypedQuery<InventoryMovement> q = em.createQuery(
            "SELECT m FROM InventoryMovement m " +
            " JOIN FETCH m.product p " +
            " JOIN FETCH p.category " +
            " JOIN FETCH p.unit " +
            " ORDER BY m.movementDate DESC",
            InventoryMovement.class
        );
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public List<InventoryMovement> findByFilter(
            Long productId,
            Long categoryId,
            Long unitId,
            LocalDate dateFrom,
            LocalDate dateTo) {

        StringBuilder jpql = new StringBuilder(
          "SELECT m FROM InventoryMovement m " +
          " JOIN FETCH m.product p " +
          " JOIN FETCH p.category " +
          " JOIN FETCH p.unit " +
          " WHERE 1=1"
        );
        if (productId != null)    jpql.append(" AND p.id = :productId");
        if (categoryId != null)   jpql.append(" AND p.category.id = :categoryId");
        if (unitId != null)       jpql.append(" AND p.unit.id = :unitId");
        if (dateFrom != null)     jpql.append(" AND m.movementDate >= :dateFrom");
        if (dateTo != null)       jpql.append(" AND m.movementDate <= :dateTo");

        TypedQuery<InventoryMovement> query = em.createQuery(jpql.toString(), InventoryMovement.class);

        if (productId != null)    query.setParameter("productId", productId);
        if (categoryId != null)   query.setParameter("categoryId", categoryId);
        if (unitId != null)       query.setParameter("unitId", unitId);
        if (dateFrom != null) {
            Instant start = dateFrom.atStartOfDay(ZoneId.systemDefault()).toInstant();
            query.setParameter("dateFrom", start);
        }
        if (dateTo != null) {
            Instant end = dateTo.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
            query.setParameter("dateTo", end);
        }

        return query.getResultList();
    }
}
