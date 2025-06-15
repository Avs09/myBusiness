package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.MovementOutputDto;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.port.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListMovementsByDateUseCase {

    private final InventoryMovementRepository movementRepo;

    /**
     * Devuelve lista de MovementOutputDto para todos los movimientos cuya
     * movementDate (Instant) esté entre dateFrom (inicio del día) y dateTo (fin del día).
     */
    public List<MovementOutputDto> execute(LocalDate dateFrom, LocalDate dateTo) {
        // Convertir LocalDate dateFrom a Instant al inicio del día:
        ZoneId zid = ZoneId.systemDefault();
        Instant fromInstant = dateFrom.atStartOfDay(zid).toInstant();
        // Convertir LocalDate dateTo a Instant al final del día:
        Instant toInstant = dateTo.plusDays(1).atStartOfDay(zid).toInstant();

        List<InventoryMovement> movimientos = movementRepo.findAll().stream()
            .filter(m -> {
                Instant inst = m.getMovementDate();
                return !inst.isBefore(fromInstant) && inst.isBefore(toInstant);
            })
            .collect(Collectors.toList());

        return movimientos.stream()
                .map(m -> MovementOutputDto.builder()
                        .id(m.getId())
                        .productId(m.getProduct().getId())
                        .movementType(m.getMovementType().name())
                        .quantity(m.getQuantity())
                        .reason(m.getReason())
                        .movementDate(m.getMovementDate())
                        .createdBy(m.getCreatedBy())
                        .createdDate(m.getCreatedDate())
                        .modifiedBy(m.getModifiedBy())
                        .modifiedDate(m.getModifiedDate())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
