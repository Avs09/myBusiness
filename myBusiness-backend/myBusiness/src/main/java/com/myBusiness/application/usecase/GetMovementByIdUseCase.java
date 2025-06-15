// src/main/java/com/myBusiness/application/usecase/GetMovementByIdUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.MovementOutputDto;
import com.myBusiness.application.exception.MovementNotFoundException;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.port.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMovementByIdUseCase {

    private final InventoryMovementRepository movRepo;

    @Transactional(readOnly = true)
    public MovementOutputDto execute(Long id) {
        InventoryMovement ent = movRepo.findById(id)
                .orElseThrow(() -> new MovementNotFoundException("Movimiento no encontrado id=" + id));
        // Mapear a DTO
        return MovementOutputDto.builder()
                .id(ent.getId())
                .productId(ent.getProduct() != null ? ent.getProduct().getId() : null)
                .productName(ent.getProduct() != null ? ent.getProduct().getName() : null)
                .movementType(ent.getMovementType().name())
                .quantity(ent.getQuantity())
                .reason(ent.getReason())
                .movementDate(ent.getMovementDate())
                .createdBy(ent.getCreatedBy())
                .createdDate(ent.getCreatedDate())
                .modifiedBy(ent.getModifiedBy())
                .modifiedDate(ent.getModifiedDate())
                .build();
    }
}
