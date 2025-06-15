package com.myBusiness.application.usecase;

import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.port.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComputeStockUseCase {

    private final InventoryMovementRepository movementRepo;

    /**
     * Calcula el stock actual de un producto sumando/restando movimientos en orden cronológico:
     *  - ENTRY: suma quantity
     *  - EXIT: resta quantity
     *  - ADJUSTMENT: fija stock = quantity
     * Nunca retorna valor negativo; en caso de suma/resta que diera <0, devuelve 0.
     */
    public BigDecimal execute(Long productId) {
        List<InventoryMovement> movs = movementRepo.findAllByProductId(productId);
        movs.sort(Comparator.comparing(InventoryMovement::getMovementDate, Comparator.nullsLast(Comparator.naturalOrder())));
        BigDecimal stock = BigDecimal.ZERO;
        for (InventoryMovement m : movs) {
            if (m.getMovementType() == null || m.getQuantity() == null) continue;
            switch (m.getMovementType()) {
                case ENTRY:
                    stock = stock.add(m.getQuantity());
                    break;
                case EXIT:
                    stock = stock.subtract(m.getQuantity());
                    break;
                case ADJUSTMENT:
                    stock = m.getQuantity();
                    break;
            }
            if (stock.compareTo(BigDecimal.ZERO) < 0) {
                stock = BigDecimal.ZERO;
            }
        }
        return stock;
    }
}
