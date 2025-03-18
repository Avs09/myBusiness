package com.myBusiness.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.myBusiness.model.InventoryMovement;

/**
 * Repository interface for InventoryMovement entities.
 * Extends JpaRepository to provide CRUD operations and pagination support.
 */
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    // JpaRepository already includes a findAll(Pageable pageable) method.
    // Se pueden agregar métodos personalizados según las necesidades del negocio, por ejemplo:
    // Page<InventoryMovement> findByType(MovementType type, Pageable pageable);
}
