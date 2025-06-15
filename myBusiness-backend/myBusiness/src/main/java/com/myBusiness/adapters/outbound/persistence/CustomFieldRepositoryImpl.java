// src/main/java/com/myBusiness/adapters/outbound/persistence/CustomFieldRepositoryImpl.java
package com.myBusiness.adapters.outbound.persistence;

import com.myBusiness.domain.model.CustomField;
import com.myBusiness.domain.port.CustomFieldRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomFieldRepositoryImpl implements CustomFieldRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public CustomField save(CustomField field) {
        if (field.getId() == null) {
            em.persist(field);
            return field;
        } else {
            return em.merge(field);
        }
    }

    @Override
    public Optional<CustomField> findById(Long id) {
        return Optional.ofNullable(em.find(CustomField.class, id));
    }

    @Override
    public List<CustomField> findAllByProductId(Long productId) {
        String jpql = "SELECT f FROM CustomField f WHERE f.productId = :pid";
        TypedQuery<CustomField> q = em.createQuery(jpql, CustomField.class);
        q.setParameter("pid", productId);
        return q.getResultList();
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(em::remove);
    }
}
