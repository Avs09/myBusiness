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
        return Optional.ofNullable(entityManager.find(Product.class, id));
    }

    @Override
    public Optional<Product> findByName(String name) {
        String query = "SELECT p FROM Product p WHERE p.name = :name";
        return entityManager.createQuery(query, Product.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<Product> findAll() {
        String query = "SELECT p FROM Product p";
        return entityManager.createQuery(query, Product.class).getResultList();
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(entityManager::remove);
    }
    
    @Override
    public Page<Product> findAll(Pageable pageable) {
        String jpql = "SELECT p FROM Product p ORDER BY p.id";
        TypedQuery<Product> q = entityManager.createQuery(jpql, Product.class);
        q.setFirstResult((int) pageable.getOffset());
        q.setMaxResults(pageable.getPageSize());
        List<Product> content = q.getResultList();
        long total = count();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public long count() {
        return entityManager.createQuery("SELECT COUNT(p) FROM Product p", Long.class)
                 .getSingleResult();
    }
}
