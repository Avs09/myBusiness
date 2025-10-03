// src/main/java/com/myBusiness/application/usecase/DeleteMovementUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.exception.MovementNotFoundException;
import com.myBusiness.domain.model.Alert;
import com.myBusiness.domain.port.AlertRepository;
import com.myBusiness.domain.port.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeleteMovementUseCase {

    private final InventoryMovementRepository movRepo;
    private final AlertRepository alertRepo;

    /**
     * Elimina un movimiento. Para evitar errores por restricción de FK con Alert,
     * primero desasocia (movement = null) todas las alertas que referencien este movimiento,
     * y luego procede a borrar el movimiento.
     */
    @Transactional
    public void execute(Long id) {
        if (!movRepo.existsById(id)) {
            throw new MovementNotFoundException("Movimiento no encontrado id=" + id);
        }

        // Desasociar alertas que referencian este movimiento
        List<Alert> allAlerts = alertRepo.findAll();
        for (Alert a : allAlerts) {
            if (a.getMovement() != null && id.equals(a.getMovement().getId())) {
                a.setMovement(null);
                alertRepo.save(a);
            }
        }

        // Eliminar movimiento
        movRepo.deleteById(id);
    }
}
