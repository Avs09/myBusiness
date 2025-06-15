// src/main/java/com/myBusiness/adapters/inbound/rest/MovementController.java
package com.myBusiness.adapters.inbound.rest;

import com.myBusiness.application.dto.*;
import com.myBusiness.application.usecase.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
public class MovementController {

    private final ListMovementsPaginatedUseCase listUseCase;
    private final CreateMovementUseCase createUseCase;
    private final UpdateMovementUseCase updateUseCase;
    private final DeleteMovementUseCase deleteUseCase;

    private final ListDailyMovementsUseCase dailyTrendUseCase;
    private final ListMovementTypeCountsUseCase typeCountsUseCase;
    private final ListRecentMovementsUseCase recentUseCase;
    private final GetMovementsLast24hCountUseCase last24hUseCase;
    private final GetTopProductsUseCase topProductsUseCase;
    private final GetStockEvolutionUseCase stockEvolutionUseCase;

    /**
     * 1) Listado paginado con filtros.
     *    GET /api/movements?page=&size=&productId=&dateFrom=&dateTo=&movementType=&search=&sort=
     */
    @GetMapping
    public ResponseEntity<PageResponseDto<MovementOutputDto>> list(@Valid MovementFilterDto filter) {
        PageResponseDto<MovementOutputDto> page = listUseCase.execute(filter);
        return ResponseEntity.ok(page);
    }

    /**
     * 2) Obtener por ID numérico
     */
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<MovementOutputDto> getById(@PathVariable("id") Long id) {
        MovementOutputDto dto = listUseCase.fetchById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * 3) Crear
     */
    @PostMapping
    public ResponseEntity<MovementOutputDto> create(@RequestBody @Valid MovementInputDto dto) {
        MovementOutputDto out = createUseCase.execute(dto);
        return ResponseEntity.status(201).body(out);
    }

    /**
     * 4) Actualizar
     */
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<MovementOutputDto> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid MovementInputDto dto) {
        MovementOutputDto out = updateUseCase.execute(id, dto);
        return ResponseEntity.ok(out);
    }

    /**
     * 5) Eliminar
     */
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    // ——— Métricas y endpoints adicionales ———

    @GetMapping("/daily-trend")
    public ResponseEntity<List<DailyMovementCountDto>> getDailyTrend(
            @RequestParam(name = "days", defaultValue = "7") int days) {
        List<DailyMovementCountDto> dto = dailyTrendUseCase.execute(days);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/type-counts")
    public ResponseEntity<List<MovementTypeCountDto>> getTypeCounts(
            @RequestParam(name = "days", defaultValue = "7") int days) {
        List<MovementTypeCountDto> dto = typeCountsUseCase.execute(days);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<MovementOutputDto>> getRecent(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        List<MovementOutputDto> dto = recentUseCase.execute(limit);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/last-24h")
    public ResponseEntity<Long> getLast24hCount() {
        Long cnt = last24hUseCase.execute();
        return ResponseEntity.ok(cnt);
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductDto>> getTopProducts(
            @RequestParam(name = "limit", defaultValue = "5") int limit) {
        List<TopProductDto> dto = topProductsUseCase.execute(limit);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/daily-inventory")
    public ResponseEntity<List<StockByDateDto>> getDailyInventory(
            @RequestParam(name = "days", defaultValue = "30") int days) {
        List<StockByDateDto> dto = stockEvolutionUseCase.execute(days);
        return ResponseEntity.ok(dto);
    }
}
