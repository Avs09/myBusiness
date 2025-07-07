// src/main/java/com/myBusiness/adapters/outbound/persistence/ProductRepositoryImpl.java
package com.myBusiness.adapters.outbound.persistence;

import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.port.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            entityManager.persist(product);
            return product;
        } else {
            return entityManager.merge(product);
        }
    }

    @Override
    public Optional<Product> findById(Long id) {
        TypedQuery<Product> q = entityManager.createQuery(
            "SELECT p FROM Product p " +
            " JOIN FETCH p.category " +
            " JOIN FETCH p.unit " +
            " WHERE p.id = :id",
            Product.class
        );
        q.setParameter("id", id);
        List<Product> list = q.getResultList();            
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list.get(0));
    }

    @Override
    public Optional<Product> findByName(String name) {
        TypedQuery<Product> q = entityManager.createQuery(
            "SELECT p FROM Product p " +
            " JOIN FETCH p.category " +
            " JOIN FETCH p.unit " +
            " WHERE p.name = :name",
            Product.class
        );
        q.setParameter("name", name);
        List<Product> list = q.getResultList();           
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list.get(0));
    }

    @Override
    public List<Product> findAll() {
        TypedQuery<Product> q = entityManager.createQuery(
            "SELECT p FROM Product p " +
            " JOIN FETCH p.category " +
            " JOIN FETCH p.unit",
            Product.class
        );
        return q.getResultList();
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(entityManager::remove);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        // 1) contenido paginado con fetch
        TypedQuery<Product> q = entityManager.createQuery(
            "SELECT p FROM Product p " +
            " JOIN FETCH p.category " +
            " JOIN FETCH p.unit " +
            " ORDER BY p.id",
            Product.class
        );
        q.setFirstResult((int) pageable.getOffset());
        q.setMaxResults(pageable.getPageSize());
        List<Product> content = q.getResultList();

        // 2) contar total (sin fetch)
        TypedQuery<Long> countQ = entityManager.createQuery(
            "SELECT COUNT(p) FROM Product p",
            Long.class
        );
        long total = countQ.getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public long count() {
        TypedQuery<Long> q = entityManager.createQuery(
            "SELECT COUNT(p) FROM Product p",
            Long.class
        );
        return q.getSingleResult();
    }
}
