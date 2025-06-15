// src/main/java/com/myBusiness/application/usecase/GetReportSummaryUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.ReportFilterDto;
import com.myBusiness.application.dto.ReportSummaryDto;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.model.MovementType;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.port.InventoryMovementRepository;
import com.myBusiness.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GetReportSummaryUseCase {

    private final InventoryMovementRepository inventoryMovementRepository;
    private final ProductRepository productRepository;

    public ReportSummaryDto execute(ReportFilterDto filter) {
        LocalDate dateTo = filter.getDateTo() != null ? filter.getDateTo() : LocalDate.now();

        Set<Long> productIds = new HashSet<>();
        if (filter.getProductId() != null) {
            productIds.add(filter.getProductId());
        } else {
            // extraer movimientos hasta dateTo según category/unit
            List<InventoryMovement> movsHastaHoy = inventoryMovementRepository.findByFilter(
                    null,
                    filter.getCategoryId(),
                    filter.getUnitId(),
                    null,
                    dateTo
            );
            for (InventoryMovement mov : movsHastaHoy) {
                if (mov.getProduct() != null && mov.getProduct().getId() != null) {
                    productIds.add(mov.getProduct().getId());
                }
            }
        }

        long totalSkusCount = 0;
        BigDecimal totalValueSum = BigDecimal.ZERO;

        LocalDate dateFromForConsumption = filter.getDateFrom() != null
                ? filter.getDateFrom()
                : dateTo.minusDays(30);
        long daysInPeriod = Math.max(1, ChronoUnit.DAYS.between(dateFromForConsumption, dateTo) + 1);

        // consumo EXIT en el período
        List<InventoryMovement> movsForConsumption = inventoryMovementRepository.findByFilter(
                null,
                filter.getCategoryId(),
                filter.getUnitId(),
                dateFromForConsumption,
                dateTo
        );
        BigDecimal totalConsumedInPeriod = BigDecimal.ZERO;
        for (InventoryMovement mov : movsForConsumption) {
            if (mov.getMovementType() == MovementType.EXIT && mov.getQuantity() != null) {
                totalConsumedInPeriod = totalConsumedInPeriod.add(mov.getQuantity());
            }
        }
        BigDecimal avgDailyConsumption = BigDecimal.ZERO;
        if (totalConsumedInPeriod.compareTo(BigDecimal.ZERO) > 0) {
            avgDailyConsumption = totalConsumedInPeriod.divide(
                    BigDecimal.valueOf(daysInPeriod),
                    8, BigDecimal.ROUND_HALF_UP);
        }

        for (Long pid : productIds) {
            if (!matchesCategoryAndUnit(pid, filter)) continue;
            BigDecimal stockActual = computeCurrentStockForProductUpTo(pid, dateTo);
            if (stockActual == null || stockActual.compareTo(BigDecimal.ZERO) <= 0) continue;
            totalSkusCount++;
            BigDecimal unitCost = fetchUnitCost(pid);
            if (unitCost != null && unitCost.compareTo(BigDecimal.ZERO) > 0) {
                totalValueSum = totalValueSum.add(stockActual.multiply(unitCost));
            }
        }

        int daysOfStock = 0;
        if (avgDailyConsumption.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal totalUnitsStock = computeTotalUnitsStock(productIds, filter, dateTo);
            if (totalUnitsStock.compareTo(BigDecimal.ZERO) > 0) {
                daysOfStock = totalUnitsStock.divide(avgDailyConsumption, 0, BigDecimal.ROUND_DOWN).intValue();
            }
        }
        String totalValueStr = totalValueSum.toPlainString();
        return new ReportSummaryDto(totalSkusCount, totalValueStr, daysOfStock);
    }

    private BigDecimal computeCurrentStockForProductUpTo(Long productId, LocalDate dateTo) {
        List<InventoryMovement> movs = inventoryMovementRepository.findByFilter(
                productId,
                null,
                null,
                null,
                dateTo
        );
        BigDecimal stock = BigDecimal.ZERO;
        for (InventoryMovement mov : movs) {
            if (mov.getQuantity() == null) continue;
            switch (mov.getMovementType()) {
                case ENTRY:
                    stock = stock.add(mov.getQuantity());
                    break;
                case EXIT:
                    stock = stock.subtract(mov.getQuantity());
                    break;
                case ADJUSTMENT:
                    stock = stock.add(mov.getQuantity());
                    break;
            }
        }
        return stock.max(BigDecimal.ZERO);
    }

    private boolean matchesCategoryAndUnit(Long productId, ReportFilterDto filter) {
        Optional<Product> opt = productRepository.findById(productId);
        if (opt.isEmpty()) return false;
        Product prod = opt.get();
        if (filter.getCategoryId() != null) {
            if (prod.getCategory() == null || prod.getCategory().getId() == null
                    || !filter.getCategoryId().equals(prod.getCategory().getId())) {
                return false;
            }
        }
        if (filter.getUnitId() != null) {
            if (prod.getUnit() == null || prod.getUnit().getId() == null
                    || !filter.getUnitId().equals(prod.getUnit().getId())) {
                return false;
            }
        }
        return true;
    }

    private BigDecimal fetchUnitCost(Long productId) {
        return productRepository.findById(productId)
                .map(Product::getPrice)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal computeTotalUnitsStock(Set<Long> productIds, ReportFilterDto filter, LocalDate dateTo) {
        BigDecimal sumUnits = BigDecimal.ZERO;
        for (Long pid : productIds) {
            if (!matchesCategoryAndUnit(pid, filter)) continue;
            BigDecimal stock = computeCurrentStockForProductUpTo(pid, dateTo);
            if (stock != null && stock.compareTo(BigDecimal.ZERO) > 0) {
                sumUnits = sumUnits.add(stock);
            }
        }
        return sumUnits;
    }
}
