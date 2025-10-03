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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListCriticalAlertsUseCase {

    private final AlertRepository alertRepo;
    private final InventoryMovementRepository movementRepo;

    /**
     * Devuelve las alertas de stock crítico (UNDERSTOCK u OVERSTOCK) vigentes HOY,
     * según el stock actual calculado del producto. No depende del estado de lectura.
     * Toma la última alerta por producto y valida si la condición sigue activa.
     */
    public List<CriticalAlertDto> execute() {
        // Traer todo el historial de alertas
        List<Alert> all = alertRepo.findAll();

        // Tomar la última alerta (por triggeredAt DESC) para cada producto
        Map<Long, Alert> latestByProduct = all.stream()
                .filter(a -> a.getProduct() != null && a.getAlertType() != null)
                .sorted(Comparator.comparing(Alert::getTriggeredAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toMap(
                        a -> a.getProduct().getId(),
                        a -> a,
                        (a1, a2) -> a1 // mantener la primera (ya es la última por el sort DESC)
                ));

        return latestByProduct.values().stream()
                .map(a -> {
                    Long pid = a.getProduct().getId();

                    // Calcular stock actual del producto
                    BigDecimal stock = BigDecimal.ZERO;
                    List<InventoryMovement> movs = movementRepo.findAllByProductId(pid);
                    movs.sort(Comparator.comparing(InventoryMovement::getMovementDate,
                            Comparator.nullsLast(Comparator.naturalOrder())));
                    for (InventoryMovement m : movs) {
                        if (m.getMovementType() == null || m.getQuantity() == null) continue;
                        switch (m.getMovementType()) {
                            case ENTRY:
                                stock = stock.add(m.getQuantity());
                                break;
                            case EXIT:
                                stock = stock.subtract(m.getQuantity());
                                break;
                            case ADJUSTMENT:
                                stock = m.getQuantity();
                                break;
                        }
                    }
                    BigDecimal currentStock = stock.max(BigDecimal.ZERO);

                    Integer min = a.getProduct().getThresholdMin();
                    Integer max = a.getProduct().getThresholdMax();

                    // Determinar si hoy está en estado crítico y el tipo correspondiente
                    AlertType computedType = null;
                    if (min != null && currentStock.compareTo(BigDecimal.valueOf(min)) < 0) {
                        computedType = AlertType.UNDERSTOCK;
                    } else if (max != null && currentStock.compareTo(BigDecimal.valueOf(max)) > 0) {
                        computedType = AlertType.OVERSTOCK;
                    }

                    if (computedType == null) return null;

                    return CriticalAlertDto.builder()
                            .id(a.getId())
                            .productId(pid)
                            .productName(a.getProduct().getName())
                            .alertType(computedType.name())
                            .triggeredAt(a.getTriggeredAt())
                            .currentStock(currentStock)
                            .thresholdMin(BigDecimal.valueOf(min != null ? min : 0))
                            .thresholdMax(BigDecimal.valueOf(max != null ? max : 0))
                            .build();
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}
