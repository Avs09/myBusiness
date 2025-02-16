package com.myBusiness.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.myBusiness.model.InventoryMovement;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    // Añadimos el método para obtener movimientos con paginación
    Page<InventoryMovement> findAll(Pageable pageable);
}
