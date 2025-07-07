// src/main/java/com/myBusiness/application/usecase/ListRecentMovementsUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.MovementOutputDto;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.port.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListRecentMovementsUseCase {

    private final InventoryMovementRepository movementRepo;

    public List<MovementOutputDto> execute(int limit) {
        List<InventoryMovement> movs = movementRepo.findTopNByOrderByMovementDateDesc(limit);

        return movs.stream()
            .map(m -> MovementOutputDto.builder()
                .id(m.getId())
                .productId(m.getProduct().getId())
                .productName(m.getProduct().getName())   // ← aquí asignamos el nombre
                .movementType(m.getMovementType().name())
                .quantity(m.getQuantity())
                .reason(m.getReason())
                .movementDate(m.getMovementDate())
                .createdBy(m.getCreatedBy())
                .createdDate(m.getCreatedDate())
                .modifiedBy(m.getModifiedBy())
                .modifiedDate(m.getModifiedDate())
                .build())
            .collect(Collectors.toList());
    }
}
