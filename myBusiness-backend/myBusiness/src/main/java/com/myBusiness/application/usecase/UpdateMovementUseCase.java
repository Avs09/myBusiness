// src/main/java/com/myBusiness/application/usecase/UpdateMovementUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.MovementInputDto;
import com.myBusiness.application.dto.MovementOutputDto;
import com.myBusiness.application.exception.InvalidMovementException;
import com.myBusiness.application.exception.MovementNotFoundException;
import com.myBusiness.domain.model.Alert;
import com.myBusiness.domain.model.AlertType;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.model.MovementType;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.port.AlertRepository;
import com.myBusiness.domain.port.InventoryMovementRepository;
import com.myBusiness.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UpdateMovementUseCase {

    private final InventoryMovementRepository movRepo;
    private final ProductRepository prodRepo;
    private final AlertRepository alertRepo;
    private final ComputeStockUseCase computeStockUseCase;

    @Transactional
    public MovementOutputDto execute(Long id, MovementInputDto dto) {
        InventoryMovement existing = movRepo.findById(id)
                .orElseThrow(() -> new MovementNotFoundException("Movimiento no encontrado id=" + id));

        Product prod = prodRepo.findById(dto.getProductId())
                .orElseThrow(() -> new InvalidMovementException("Producto no encontrado id=" + dto.getProductId()));

        MovementType type;
        try {
            type = MovementType.valueOf(dto.getMovementType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMovementException("Tipo de movimiento inválido: " + dto.getMovementType());
        }

        existing.setProduct(prod);
        existing.setMovementType(type);
        existing.setQuantity(dto.getQuantity());
        existing.setReason(dto.getReason());
        InventoryMovement updated = movRepo.save(existing);

        // Después de actualizar, verificar stock y crear alerta si aplica
        BigDecimal stockActual = computeStockUseCase.execute(prod.getId());
        Integer min = prod.getThresholdMin();
        Integer max = prod.getThresholdMax();
        if (min != null && stockActual.compareTo(BigDecimal.valueOf(min)) < 0) {
            Alert alert = Alert.builder()
                    .product(prod)
                    .alertType(AlertType.UNDERSTOCK)
                    .movement(updated)
                    .build();
            alertRepo.save(alert);
        } else if (max != null && stockActual.compareTo(BigDecimal.valueOf(max)) > 0) {
            Alert alert = Alert.builder()
                    .product(prod)
                    .alertType(AlertType.OVERSTOCK)
                    .movement(updated)
                    .build();
            alertRepo.save(alert);
        }

        return MovementOutputDto.builder()
                .id(updated.getId())
                .productId(updated.getProduct().getId())
                .productName(updated.getProduct().getName())
                .movementType(updated.getMovementType().name())
                .quantity(updated.getQuantity())
                .reason(updated.getReason())
                .movementDate(updated.getMovementDate())
                .createdBy(updated.getCreatedBy())
                .createdDate(updated.getCreatedDate())
                .modifiedBy(updated.getModifiedBy())
                .modifiedDate(updated.getModifiedDate())
                .build();
    }
}
