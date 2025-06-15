// src/main/java/com/myBusiness/domain/port/InventoryMovementRepository.java
package com.myBusiness.domain.port;

import com.myBusiness.domain.model.InventoryMovement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventoryMovementRepository {
    InventoryMovement save(InventoryMovement movement);
    Optional<InventoryMovement> findById(Long id);
    boolean existsById(Long id);
    List<InventoryMovement> findAllByProductId(Long productId);
    void deleteById(Long id);

    /**
     * Filtrado por producto, categoría, unidad y rango de fechas (LocalDate).
     * dateFrom/dateTo incluyentes. Si un parámetro es null, no se filtra por él.
     */
    List<InventoryMovement> findByFilter(
        Long productId,
        Long categoryId,
        Long unitId,
        LocalDate dateFrom,
        LocalDate dateTo
    );

    List<InventoryMovement> findAll();
    List<InventoryMovement> findTopNByOrderByMovementDateDesc(int limit);
}
