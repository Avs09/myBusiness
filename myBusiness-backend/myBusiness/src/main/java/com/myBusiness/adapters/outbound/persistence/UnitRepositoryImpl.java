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
    private EntityManager entityManager;

    @Override
    public Unit save(Unit unit) {
        if (unit.getId() == null) {
            entityManager.persist(unit);
            return unit;
        } else {
            return entityManager.merge(unit);
        }
    }

    @Override
    public Optional<Unit> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Unit.class, id));
    }

	@Override
	public void deleteById(Long id) {
		findById(id).ifPresent(entityManager::remove);
	}
	
	@Override
	public List<Unit> findAll() {
	    TypedQuery<Unit> query = entityManager.createQuery(
	        "SELECT u FROM Unit u", Unit.class
	    );
	    return query.getResultList();
	}
}
