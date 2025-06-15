// src/main/java/com/myBusiness/application/usecase/CreateAlertUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.AlertInputDto;
import com.myBusiness.application.dto.AlertOutputDto;
import com.myBusiness.application.exception.InvalidAlertException;
import com.myBusiness.domain.model.Alert;
import com.myBusiness.domain.model.AlertType;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.port.AlertRepository;
import com.myBusiness.domain.port.ProductRepository;
import com.myBusiness.domain.port.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateAlertUseCase {

    private final AlertRepository alertRepo;
    private final ProductRepository productRepo;
    private final InventoryMovementRepository movementRepo;

    @Transactional
    public AlertOutputDto execute(AlertInputDto dto) {
        // 1. Validar producto existente
        Product prod = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new InvalidAlertException("Producto no encontrado id=" + dto.getProductId()));

        // 2. Validar tipo de alerta
        AlertType type;
        try {
            type = AlertType.valueOf(dto.getAlertType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidAlertException("Tipo de alerta inválido: " + dto.getAlertType());
        }

        // 3. Construir entidad Alert
        Alert.AlertBuilder builder = Alert.builder()
                .product(prod)
                .alertType(type);
        if (dto.getMovementId() != null) {
            InventoryMovement mov = movementRepo.findById(dto.getMovementId())
                    .orElseThrow(() -> new InvalidAlertException("Movimiento no encontrado id=" + dto.getMovementId()));
            builder.movement(mov);
        }
        Alert ent = builder.build();
        Alert saved = alertRepo.save(ent);
        return toDto(saved);
    }

    private AlertOutputDto toDto(Alert a) {
        Integer min = null, max = null;
        if (a.getProduct() != null) {
            min = a.getProduct().getThresholdMin();
            max = a.getProduct().getThresholdMax();
        }
        return AlertOutputDto.builder()
                .id(a.getId())
                .productId(a.getProduct() != null ? a.getProduct().getId() : null)
                .productName(a.getProduct() != null ? a.getProduct().getName() : null)
                .movementId(a.getMovement() != null ? a.getMovement().getId() : null)
                .alertType(a.getAlertType().name())
                .triggeredAt(a.getTriggeredAt())
                .createdDate(a.getCreatedDate())
                .thresholdMin(min)
                .thresholdMax(max)
                .build();
    }
}
