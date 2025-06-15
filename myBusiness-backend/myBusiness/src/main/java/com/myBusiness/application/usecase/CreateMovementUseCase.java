// src/main/java/com/myBusiness/application/usecase/CreateMovementUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.MovementInputDto;
import com.myBusiness.application.dto.MovementOutputDto;
import com.myBusiness.application.exception.InvalidMovementException;
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
public class CreateMovementUseCase {

    private final InventoryMovementRepository movRepo;
    private final ProductRepository prodRepo;
    private final AlertRepository alertRepo;
    private final ComputeStockUseCase computeStockUseCase;

    @Transactional
    public MovementOutputDto execute(MovementInputDto dto) {
        Product prod = prodRepo.findById(dto.getProductId())
                .orElseThrow(() -> new InvalidMovementException("Producto no encontrado id=" + dto.getProductId()));

        MovementType type;
        try {
            type = MovementType.valueOf(dto.getMovementType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMovementException("Tipo de movimiento inválido: " + dto.getMovementType());
        }

        InventoryMovement ent = InventoryMovement.builder()
                .product(prod)
                .movementType(type)
                .quantity(dto.getQuantity())
                .reason(dto.getReason())
                .build();
        InventoryMovement saved = movRepo.save(ent);

        // Después de guardar, verificar stock y crear alerta si aplica
        BigDecimal stockActual = computeStockUseCase.execute(prod.getId());
        Integer min = prod.getThresholdMin();
        Integer max = prod.getThresholdMax();
        if (min != null && stockActual.compareTo(BigDecimal.valueOf(min)) < 0) {
            Alert alert = Alert.builder()
                    .product(prod)
                    .alertType(AlertType.UNDERSTOCK)
                    .movement(saved)
                    .build();
            alertRepo.save(alert);
        } else if (max != null && stockActual.compareTo(BigDecimal.valueOf(max)) > 0) {
            Alert alert = Alert.builder()
                    .product(prod)
                    .alertType(AlertType.OVERSTOCK)
                    .movement(saved)
                    .build();
            alertRepo.save(alert);
        }

        return toDto(saved);
    }

    private MovementOutputDto toDto(InventoryMovement m) {
        return MovementOutputDto.builder()
                .id(m.getId())
                .productId(m.getProduct().getId())
                .productName(m.getProduct().getName())
                .movementType(m.getMovementType().name())
                .quantity(m.getQuantity())
                .reason(m.getReason())
                .movementDate(m.getMovementDate())
                .createdBy(m.getCreatedBy())
                .createdDate(m.getCreatedDate())
                .modifiedBy(m.getModifiedBy())
                .modifiedDate(m.getModifiedDate())
                .build();
    }
}
