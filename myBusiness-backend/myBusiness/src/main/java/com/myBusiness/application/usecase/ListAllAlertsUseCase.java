package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.AlertOutputDto;
import com.myBusiness.domain.model.Alert;
import com.myBusiness.domain.port.AlertRepository;
import com.myBusiness.domain.port.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListAllAlertsUseCase {
    private final AlertRepository alertRepo;
    private final InventoryMovementRepository movementRepo;

    public List<AlertOutputDto> execute() {
        return alertRepo.findAll().stream()
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