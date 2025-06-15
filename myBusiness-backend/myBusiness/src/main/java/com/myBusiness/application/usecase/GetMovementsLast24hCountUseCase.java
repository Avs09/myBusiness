package com.myBusiness.application.usecase;

import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.port.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMovementsLast24hCountUseCase {

    private final InventoryMovementRepository movementRepo;

    /**
     * Devuelve la cantidad (long) de movimientos cuya fecha (movementDate)
     * está dentro de las últimas 24 horas.
     */
    public long execute() {
        Instant cutoff = Instant.now().minus(Duration.ofHours(24));
        List<InventoryMovement> all = movementRepo.findAll();
        return all.stream()
                  .filter(m -> m.getMovementDate().isAfter(cutoff))
                  .count();
    }
}
