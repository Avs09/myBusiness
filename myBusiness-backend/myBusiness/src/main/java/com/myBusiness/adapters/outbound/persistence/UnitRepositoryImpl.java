package com.myBusiness.adapters.outbound.persistence;

import com.myBusiness.domain.model.Unit;
import com.myBusiness.domain.port.UnitRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UnitRepositoryImpl implements UnitRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Unit save(Unit unit) {
        if (unit.getId() == null) {
            em.persist(unit);
            return unit;
        } else {
            return em.merge(unit);
        }
    }

    @Override
    public Optional<Unit> findById(Long id) {
        return Optional.ofNullable(em.find(Unit.class, id));
    }

    @Override
    public List<Unit> findAll() {
        TypedQuery<Unit> q = em.createQuery(
            "SELECT u FROM Unit u ORDER BY u.name",
            Unit.class
        );
        return q.getResultList();
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(em::remove);
    }
}
