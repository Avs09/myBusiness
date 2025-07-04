// src/main/java/com/myBusiness/application/usecase/ListAlertsUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.AlertOutputDto;
import com.myBusiness.domain.model.Alert;
import com.myBusiness.domain.port.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListAlertsUseCase {
    private final AlertRepository alertRepo;

    /**
     * Devuelve histórico completo de alertas automáticas.
     */
    public List<AlertOutputDto> execute() {
        return alertRepo.findAll().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    private AlertOutputDto toDto(Alert a) {
        Integer min = null, max = null;
        if (a.getProduct() != null) {
            min = a.getProduct().getThresholdMin();
            max = a.getProduct().getThresholdMax();
        }
        return AlertOutputDto.builder()
                .id(a.getId())
                .productId(a.getProduct().getId())
                .productName(a.getProduct().getName())
                .movementId(a.getMovement() != null ? a.getMovement().getId() : null)
                .alertType(a.getAlertType().name())
                .triggeredAt(a.getTriggeredAt())
                .createdDate(a.getCreatedDate())
                .thresholdMin(min)
                .thresholdMax(max)
                .build();
    }
}
