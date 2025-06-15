// src/main/java/com/myBusiness/application/usecase/DeleteMovementUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.exception.MovementNotFoundException;
import com.myBusiness.domain.port.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteMovementUseCase {

    private final InventoryMovementRepository movRepo;

    @Transactional
    public void execute(Long id) {
        if (!movRepo.existsById(id)) {
            throw new MovementNotFoundException("Movimiento no encontrado id=" + id);
        }
        movRepo.deleteById(id);
    }
}
