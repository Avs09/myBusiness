package com.myBusiness.adapters.outbound.persistence;

import com.myBusiness.domain.model.Category;
import com.myBusiness.domain.port.CategoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Category save(Category category) {
        if (category.getId() == null) {
            em.persist(category);
            return category;
        } else {
            return em.merge(category);
        }
    }

    @Override
    public Optional<Category> findById(Long id) {
        Optional<Category> cat = Optional.ofNullable(em.find(Category.class, id));
        // si quisieras inicializar relaciones, podrías usar JOIN FETCH en query
        return cat;
    }

    @Override
    public List<Category> findAll() {
        TypedQuery<Category> q = em.createQuery(
            "SELECT c FROM Category c ORDER BY c.name",
            Category.class
        );
        return q.getResultList();
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(em::remove);
    }
}
