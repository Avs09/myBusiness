package com.myBusiness.adapters.inbound.rest;

import com.myBusiness.application.dto.CategoryInputDto;
import com.myBusiness.application.dto.CategoryOutputDto;
import com.myBusiness.application.dto.CategorySummaryDto;       // <<< IMPORT
import com.myBusiness.application.usecase.CreateCategoryUseCase;
import com.myBusiness.application.usecase.DeleteCategoryUseCase;
import com.myBusiness.application.usecase.GetCategorySummaryUseCase;  // <<< IMPORT
import com.myBusiness.application.usecase.GetCategoryUseCase;
import com.myBusiness.application.usecase.ListCategoriesUseCase;
import com.myBusiness.application.usecase.UpdateCategoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CreateCategoryUseCase createUseCase;
    private final GetCategoryUseCase getUseCase;
    private final ListCategoriesUseCase listUseCase;
    private final UpdateCategoryUseCase updateUseCase;
    private final DeleteCategoryUseCase deleteUseCase;

    // Único lugar donde se expone /api/categories/summary
    private final GetCategorySummaryUseCase summaryUseCase;

    @PostMapping
    public ResponseEntity<CategoryOutputDto> create(@RequestBody @Validated CategoryInputDto input) {
        CategoryOutputDto out = createUseCase.execute(input);
        URI location = URI.create("/api/categories/" + out.getId());
        return ResponseEntity.created(location).body(out);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryOutputDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(getUseCase.execute(id));
    }

    @GetMapping
    public ResponseEntity<List<CategoryOutputDto>> listAll() {
        return ResponseEntity.ok(listUseCase.execute());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryOutputDto> update(
            @PathVariable Long id,
            @RequestBody @Validated CategoryInputDto input
    ) {
        return ResponseEntity.ok(updateUseCase.execute(id, input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * ——— Resumen por Categoría ———
     * GET /api/categories/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<List<CategorySummaryDto>> getSummary() {
        List<CategorySummaryDto> dto = summaryUseCase.execute();
        return ResponseEntity.ok(dto);
    }
}
