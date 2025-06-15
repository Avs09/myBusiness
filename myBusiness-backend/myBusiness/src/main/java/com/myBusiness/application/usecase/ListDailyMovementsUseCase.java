package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.DailyMovementCountDto;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.port.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListDailyMovementsUseCase {

    private final InventoryMovementRepository movementRepo;

    /**
     * Cuenta cuántos movimientos hubo cada día en los últimos `days` días (incluyendo hoy).
     */
    public List<DailyMovementCountDto> execute(int days) {
        ZoneId zid = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zid);
        LocalDate fromDate = today.minusDays(days - 1);

        // 1) Traer movimientos en ese rango de fechas
        List<InventoryMovement> todos = movementRepo.findByFilter(
            null, 
            null, 
            null, 
            fromDate,
            today
        );

        // 2) Agrupar por LocalDate directamente
        Map<LocalDate, Long> countsPorDia = todos.stream()
            .collect(Collectors.groupingBy(
                m -> m.getMovementDate().atZone(zid).toLocalDate(),
                Collectors.counting()
            ));

        // 3) Llenar lista con cero para fechas sin movimientos
        List<DailyMovementCountDto> resultado = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate ld = fromDate.plusDays(i);
            long count = countsPorDia.getOrDefault(ld, 0L);
            resultado.add(DailyMovementCountDto.builder()
                .date(ld.toString())
                .count(count)
                .build()
            );
        }

        return resultado;
    }
}
