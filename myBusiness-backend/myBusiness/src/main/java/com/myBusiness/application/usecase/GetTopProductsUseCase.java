package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.TopProductDto;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.port.InventoryMovementRepository;
import com.myBusiness.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetTopProductsUseCase {

    private final InventoryMovementRepository movementRepo;
    private final ProductRepository productRepo;

    /**
     * Devuelve top N productos con mayor suma de EXIT en los últimos 30 días.
     */
    public List<TopProductDto> execute(int topN) {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);
        LocalDate thirtyDaysAgo = today.minusDays(30);

        List<InventoryMovement> inRange = movementRepo.findByFilter(
            null, null, null,
            thirtyDaysAgo,
            today
        );

        Map<Long, Long> sumByProduct = inRange.stream()
            .filter(m -> m.getMovementType() != null && m.getMovementType().name().equals("EXIT"))
            .collect(Collectors.groupingBy(
                m -> m.getProduct().getId(),
                Collectors.summingLong(m -> m.getQuantity().longValue())
            ));

        return sumByProduct.entrySet().stream()
            .sorted(Map.Entry.<Long, Long>comparingByValue(Comparator.reverseOrder()))
            .limit(topN)
            .map(entry -> {
                Long prodId = entry.getKey();
                long total = entry.getValue();
                String name = productRepo.findById(prodId).map(p -> p.getName()).orElse("N/D");
                return TopProductDto.builder()
                        .productId(prodId)
                        .productName(name)
                        .totalExceeded(total)
                        .build();
            })
            .collect(Collectors.toList());
    }
}
