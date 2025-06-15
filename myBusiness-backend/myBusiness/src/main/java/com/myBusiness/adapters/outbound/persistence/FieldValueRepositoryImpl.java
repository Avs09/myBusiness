// src/main/java/com/myBusiness/adapters/outbound/persistence/FieldValueRepositoryImpl.java
package com.myBusiness.adapters.outbound.persistence;

import com.myBusiness.domain.model.FieldValue;
import com.myBusiness.domain.port.FieldValueRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FieldValueRepositoryImpl implements FieldValueRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public FieldValue save(FieldValue value) {
        if (value.getId() == null) {
            em.persist(value);
            return value;
        } else {
            return em.merge(value);
        }
    }

    @Override
    public Optional<FieldValue> findById(Long id) {
        return Optional.ofNullable(em.find(FieldValue.class, id));
    }

    @Override
    public List<FieldValue> findAllByProductIdAndFieldId(Long productId, Long fieldId) {
        String jpql = "SELECT v FROM FieldValue v WHERE v.productId = :pid AND v.field.id = :fid";
        TypedQuery<FieldValue> q = em.createQuery(jpql, FieldValue.class);
        q.setParameter("pid", productId);
        q.setParameter("fid", fieldId);
        return q.getResultList();
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(em::remove);
    }
}
