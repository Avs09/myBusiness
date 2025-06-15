package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.MovementTypeCountDto;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.port.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListMovementTypeCountsUseCase {

    private final InventoryMovementRepository movementRepo;

    /**
     * Cuenta cuántos movimientos de cada tipo hubo en los últimos `days` días.
     */
    public List<MovementTypeCountDto> execute(int days) {
        ZoneId zid = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zid);
        LocalDate fromDate = today.minusDays(days - 1);

        List<InventoryMovement> movs = movementRepo.findByFilter(
            null, null, null,
            fromDate,
            today
        );

        Map<String, Long> countsByType = movs.stream()
            .collect(Collectors.groupingBy(
                m -> m.getMovementType().name(),
                Collectors.counting()
            ));

        return countsByType.entrySet().stream()
            .map(e -> MovementTypeCountDto.builder()
                .movementType(e.getKey())
                .count(e.getValue())
                .build())
            .collect(Collectors.toList());
    }
}
