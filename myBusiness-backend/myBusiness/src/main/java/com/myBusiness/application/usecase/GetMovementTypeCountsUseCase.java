// src/main/java/com/myBusiness/application/usecase/GetMovementTypeCountsUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.MovementTypeCountDto;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.port.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetMovementTypeCountsUseCase {

    private final InventoryMovementRepository movementRepo;

    public List<MovementTypeCountDto> execute(int days) {
        ZoneId zid = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zid);
        LocalDate fromDate = today.minusDays(days - 1);

        List<InventoryMovement> todos = movementRepo.findByFilter(
            null,
            null,
            null,
            fromDate,
            today
        );

        Map<String, Long> countsPorTipo = todos.stream()
            .collect(Collectors.groupingBy(
                mv -> mv.getMovementType().name(),
                Collectors.counting()
            ));

        List<MovementTypeCountDto> resultado = new ArrayList<>();
        for (String tipo : List.of("ENTRY", "EXIT", "ADJUSTMENT")) {
            long cnt = countsPorTipo.getOrDefault(tipo, 0L);
            resultado.add(MovementTypeCountDto.builder()
                .movementType(tipo)
                .count(cnt)
                .build());
        }
        return resultado;
    }
}
