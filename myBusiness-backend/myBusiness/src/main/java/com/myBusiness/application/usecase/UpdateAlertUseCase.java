// src/main/java/com/myBusiness/application/usecase/UpdateAlertUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.AlertInputDto;
import com.myBusiness.application.dto.AlertOutputDto;
import com.myBusiness.application.exception.AlertNotFoundException;
import com.myBusiness.application.exception.InvalidAlertException;
import com.myBusiness.domain.model.Alert;
import com.myBusiness.domain.model.AlertType;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.port.AlertRepository;
import com.myBusiness.domain.port.InventoryMovementRepository;
import com.myBusiness.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAlertUseCase {

    private final AlertRepository alertRepo;
    private final ProductRepository productRepo;
    private final InventoryMovementRepository movementRepo;

    @Transactional
    public AlertOutputDto execute(Long alertId, AlertInputDto dto) {
        Alert existing = alertRepo.findById(alertId)
            .orElseThrow(() -> new AlertNotFoundException("Alerta no encontrada id=" + alertId));

        Product prod = productRepo.findById(dto.getProductId())
            .orElseThrow(() -> new InvalidAlertException("Producto no encontrado id=" + dto.getProductId()));

        AlertType type;
        try {
            type = AlertType.valueOf(dto.getAlertType().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidAlertException("Tipo de alerta inválido: " + dto.getAlertType());
        }
        existing.setProduct(prod);
        existing.setAlertType(type);
        if (dto.getMovementId() != null) {
            InventoryMovement mov = movementRepo.findById(dto.getMovementId())
                .orElseThrow(() -> new InvalidAlertException("Movimiento no encontrado id=" + dto.getMovementId()));
            existing.setMovement(mov);
        } else {
            existing.setMovement(null);
        }
        Alert saved = alertRepo.save(existing);
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
