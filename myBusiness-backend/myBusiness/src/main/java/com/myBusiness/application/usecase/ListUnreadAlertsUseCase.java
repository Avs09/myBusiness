// src/main/java/com/myBusiness/application/usecase/ListUnreadAlertsUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.AlertOutputDto;
import com.myBusiness.domain.model.Alert;
import com.myBusiness.domain.port.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListUnreadAlertsUseCase {
    private final AlertRepository alertRepo;

    /**
     * Obtiene todas las alertas no leídas, las marca como leídas para que no reaparezcan
     * en próximos polls, y devuelve sus DTOs.
     */
    @Transactional
    public List<AlertOutputDto> execute() {
        List<Alert> unread = alertRepo.findAllUnread();
        // Marcar como leídas para que no aparezcan en próximas consultas
        unread.forEach(a -> {
            a.setRead(true);
            alertRepo.save(a);
        });
        return unread.stream()
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
                .productId(a.getProduct() != null ? a.getProduct().getId() : null)
                .productName(a.getProduct() != null ? a.getProduct().getName() : null)
                .movementId(a.getMovement() != null ? a.getMovement().getId() : null)
                .alertType(a.getAlertType() != null ? a.getAlertType().name() : null)
                .triggeredAt(a.getTriggeredAt())
                .createdDate(a.getCreatedDate())
                .thresholdMin(min)
                .thresholdMax(max)
                .build();
    }
}
