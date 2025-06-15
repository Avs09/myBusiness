// src/main/java/com/myBusiness/application/usecase/GetStockEvolutionUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.StockByDateDto;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.port.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetStockEvolutionUseCase {

    private final InventoryMovementRepository movementRepo;

    public List<StockByDateDto> execute(int days) {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);
        LocalDate start = today.minusDays(days - 1);

        // Obtener movimientos en el rango [start, today]
        List<InventoryMovement> movs = movementRepo.findByFilter(
            null,
            null,
            null,
            start,
            today
        );

        // Ordenar por movementDate asc
        movs.sort(Comparator.comparing(InventoryMovement::getMovementDate));

        Map<LocalDate, BigDecimal> stockAtEndOfDay = new TreeMap<>();
        BigDecimal runningStock = BigDecimal.ZERO;

        // Para cada día, recalcular acumulado hasta fin de día
        for (int i = 0; i < days; i++) {
            LocalDate currentDay = start.plusDays(i);
            LocalDateTime endOfDay = currentDay.atTime(LocalTime.MAX);
            Instant endInstant = endOfDay.atZone(zone).toInstant();

            // Filtrar los movimientos cuya fecha <= endInstant
            BigDecimal dayStock = BigDecimal.ZERO;
            for (InventoryMovement m : movs) {
                if (!m.getMovementDate().isAfter(endInstant)) {
                    switch (m.getMovementType()) {
                        case ENTRY:
                            dayStock = dayStock.add(m.getQuantity());
                            break;
                        case EXIT:
                            dayStock = dayStock.subtract(m.getQuantity());
                            break;
                        case ADJUSTMENT:
                            dayStock = m.getQuantity();
                            break;
                    }
                } else {
                    break; // movs está ordenado, así que el resto es posterior
                }
            }
            stockAtEndOfDay.put(currentDay, dayStock.max(BigDecimal.ZERO));
        }

        return stockAtEndOfDay.entrySet().stream()
            .map(e -> StockByDateDto.builder()
                    .date(e.getKey().toString())
                    .totalStock(e.getValue())
                    .build())
            .collect(Collectors.toList());
    }
}
