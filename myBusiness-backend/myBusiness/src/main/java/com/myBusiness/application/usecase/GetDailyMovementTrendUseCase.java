// src/main/java/com/myBusiness/application/usecase/GetDailyMovementTrendUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.DailyMovementCountDto;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.port.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetDailyMovementTrendUseCase {

    private final InventoryMovementRepository movementRepo;

    public List<DailyMovementCountDto> execute(int days) {
        ZoneId zid = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zid);
        LocalDate fromDate = today.minusDays(days - 1);

        // Obtener movimientos en el rango [fromDate, today]
        List<InventoryMovement> todos = movementRepo.findByFilter(
            null,
            null,
            null,
            fromDate,
            today
        );

        // Agrupar por LocalDate de movementDate
        Map<LocalDate, Long> countsPorDia = todos.stream()
            .collect(Collectors.groupingBy(
                mv -> Instant.ofEpochMilli(mv.getMovementDate().toEpochMilli())
                             .atZone(zid)
                             .toLocalDate(),
                Collectors.counting()
            ));

        List<DailyMovementCountDto> resultado = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate ld = fromDate.plusDays(i);
            long count = countsPorDia.getOrDefault(ld, 0L);
            resultado.add(DailyMovementCountDto.builder()
                .date(ld.toString())
                .count(count)
                .build());
        }
        return resultado;
    }
}
