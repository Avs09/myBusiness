// src/main/java/com/myBusiness/adapters/inbound/rest/DashboardController.java
package com.myBusiness.adapters.inbound.rest;

import com.myBusiness.application.dto.DashboardMetricsDto;
import com.myBusiness.application.dto.StockByDateDto;
import com.myBusiness.application.dto.TopProductDto;
import com.myBusiness.application.usecase.GetDashboardMetricsUseCase;
import com.myBusiness.application.usecase.GetStockEvolutionUseCase;
import com.myBusiness.application.usecase.GetTopProductsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final GetDashboardMetricsUseCase getMetricsUseCase;
    private final GetStockEvolutionUseCase stockEvolutionUseCase;
    private final GetTopProductsUseCase topProductsUseCase;

    @GetMapping("/metrics")
    public ResponseEntity<DashboardMetricsDto> getMetrics() {
        DashboardMetricsDto dto = getMetricsUseCase.execute();
        return ResponseEntity.ok(dto);
    }

    /**
     * GET /api/dashboard/stock-evolution?days=7
     */
    @GetMapping("/stock-evolution")
    public ResponseEntity<List<StockByDateDto>> getStockEvolution(
        @RequestParam(name = "days", defaultValue = "7") int days
    ) {
        List<StockByDateDto> dto = stockEvolutionUseCase.execute(days);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductDto>> getTopProducts(
        @RequestParam(name = "limit", defaultValue = "5") int limit
    ) {
        List<TopProductDto> dto = topProductsUseCase.execute(limit);
        return ResponseEntity.ok(dto);
    }
}
