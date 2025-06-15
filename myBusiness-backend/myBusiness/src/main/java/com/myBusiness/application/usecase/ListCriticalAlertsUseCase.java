// src/main/java/com/myBusiness/application/usecase/ListCriticalAlertsUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.CriticalAlertDto;
import com.myBusiness.domain.model.Alert;
import com.myBusiness.domain.model.AlertType;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.port.AlertRepository;
import com.myBusiness.domain.port.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListCriticalAlertsUseCase {
    private final AlertRepository alertRepo;
    private final InventoryMovementRepository movementRepo;

    /**
     * Obtiene alertas críticas actuales (UNDERSTOCK o OVERSTOCK) no leídas.
     * Marca como leídas aquellas cuya violación ya se resolvió; retorna solo las que persisten.
     */
    public List<CriticalAlertDto> execute() {
        // obtiene todas no leídas
        List<Alert> unread = alertRepo.findAllUnread();
        // filtrar solo UNDERSTOCK/OVERSTOCK y vigentes
        return unread.stream()
            .filter(a -> a.getAlertType() == AlertType.UNDERSTOCK || a.getAlertType() == AlertType.OVERSTOCK)
            .filter(a -> {
                // calcular stock actual
                Long pid = a.getProduct().getId();
                BigDecimal stock = BigDecimal.ZERO;
                List<InventoryMovement> movs = movementRepo.findAllByProductId(pid);
                movs.sort(Comparator.comparing(InventoryMovement::getMovementDate));
                for (InventoryMovement m : movs) {
                    switch (m.getMovementType()) {
                        case ENTRY:    stock = stock.add(m.getQuantity()); break;
                        case EXIT:     stock = stock.subtract(m.getQuantity()); break;
                        case ADJUSTMENT: stock = m.getQuantity(); break;
                    }
                }
                BigDecimal currentStock = stock.max(BigDecimal.ZERO);
                Integer min = a.getProduct().getThresholdMin();
                Integer max = a.getProduct().getThresholdMax();
                if (a.getAlertType() == AlertType.UNDERSTOCK) {
                    return min != null && currentStock.compareTo(BigDecimal.valueOf(min)) < 0;
                } else {
                    return max != null && currentStock.compareTo(BigDecimal.valueOf(max)) > 0;
                }
            })
            .map(a -> {
                Long pid = a.getProduct().getId();
                BigDecimal stock = BigDecimal.ZERO;
                List<InventoryMovement> movs = movementRepo.findAllByProductId(pid);
                movs.sort(Comparator.comparing(InventoryMovement::getMovementDate));
                for (InventoryMovement m : movs) {
                    switch (m.getMovementType()) {
                        case ENTRY:    stock = stock.add(m.getQuantity()); break;
                        case EXIT:     stock = stock.subtract(m.getQuantity()); break;
                        case ADJUSTMENT: stock = m.getQuantity(); break;
                    }
                }
                BigDecimal currentStock = stock.max(BigDecimal.ZERO);
                Integer min = a.getProduct().getThresholdMin();
                Integer max = a.getProduct().getThresholdMax();
                return CriticalAlertDto.builder()
                        .id(a.getId())
                        .productId(pid)
                        .productName(a.getProduct().getName())
                        .alertType(a.getAlertType().name())
                        .triggeredAt(a.getTriggeredAt())
                        .currentStock(currentStock)
                        .thresholdMin(BigDecimal.valueOf(min != null ? min : 0))
                        .thresholdMax(BigDecimal.valueOf(max != null ? max : 0))
                        .build();
            })
            .collect(Collectors.toList());
    }
}
