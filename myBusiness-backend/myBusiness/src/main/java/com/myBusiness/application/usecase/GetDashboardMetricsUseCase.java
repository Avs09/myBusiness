package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.DashboardMetricsDto;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.port.AlertRepository;
import com.myBusiness.domain.port.InventoryMovementRepository;
import com.myBusiness.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetDashboardMetricsUseCase {

    private final ProductRepository productRepo;
    private final InventoryMovementRepository movRepo;
    private final AlertRepository alertRepo;
    private final ComputeStockUseCase computeStockUseCase;

    public DashboardMetricsDto execute() {
        long totalProducts = productRepo.count();

        BigDecimal totalValue = BigDecimal.ZERO;
        List<Product> products = productRepo.findAll();
        for (Product p : products) {
            BigDecimal stockActual = computeStockUseCase.execute(p.getId());
            BigDecimal precio = p.getPrice() != null ? p.getPrice() : BigDecimal.ZERO;
            totalValue = totalValue.add(precio.multiply(stockActual));
        }

        long totalOpenAlerts = alertRepo.findAllUnread().size();

        Instant cutoff7 = Instant.now().minus(7, ChronoUnit.DAYS);
        long movementsLast7Days = movRepo.findAll().stream()
            .map(InventoryMovement::getMovementDate)
            .filter(d -> d != null && d.isAfter(cutoff7))
            .count();

        return DashboardMetricsDto.builder()
                .totalProducts(totalProducts)
                .totalInventoryValue(totalValue.toPlainString())
                .totalOpenAlerts(totalOpenAlerts)
                .movementsLast7Days(movementsLast7Days)
                .build();
    }
}
