package com.myBusiness.domain.port;

import com.myBusiness.domain.model.Unit;

import java.util.List;
import java.util.Optional;

public interface UnitRepository {
    Unit save(Unit unit);
    Optional<Unit> findById(Long id);
    void deleteById(Long id);   
    List<Unit> findAll();
}
