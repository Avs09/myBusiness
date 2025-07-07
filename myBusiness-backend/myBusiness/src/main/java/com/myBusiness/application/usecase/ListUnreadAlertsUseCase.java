package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.AlertOutputDto;
import com.myBusiness.domain.model.Alert;
import com.myBusiness.domain.port.AlertRepository;
import com.myBusiness.domain.port.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListUnreadAlertsUseCase {
    private final AlertRepository alertRepo;
    private final InventoryMovementRepository movementRepo;

    /**
     * Obtiene todas las alertas no leídas, las marca como leídas y devuelve DTO incluyendo stock.
     */
    @Transactional
    public List<AlertOutputDto> execute() {
        List<Alert> unread = alertRepo.findAllUnread();
        // Marcar como leídas
        unread.forEach(a -> {
            a.setRead(true);
            alertRepo.save(a);
        });

        return unread.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private AlertOutputDto toDto(Alert a) {
        BigDecimal stock = BigDecimal.ZERO;
        var movs = movementRepo.findAllByProductId(a.getProduct().getId());
        movs.sort(Comparator.comparing(m -> m.getMovementDate()));
        for (var m : movs) {
            switch (m.getMovementType()) {
                case ENTRY:    stock = stock.add(m.getQuantity()); break;
                case EXIT:     stock = stock.subtract(m.getQuantity()); break;
                case ADJUSTMENT: stock = m.getQuantity(); break;
            }
        }
        BigDecimal currentStock = stock.max(BigDecimal.ZERO);
        Integer min = a.getProduct().getThresholdMin();
        Integer max = a.getProduct().getThresholdMax();

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
                .currentStock(currentStock)
                .build();
    }
}