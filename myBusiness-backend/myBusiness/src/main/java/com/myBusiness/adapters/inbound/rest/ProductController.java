// src/main/java/com/myBusiness/adapters/inbound/rest/ProductController.java
package com.myBusiness.adapters.inbound.rest;

import com.myBusiness.application.dto.PageResponseDto;
import com.myBusiness.application.dto.ProductInputDto;
import com.myBusiness.application.dto.ProductOutputDto;
import com.myBusiness.application.usecase.CreateProductUseCase;
import com.myBusiness.application.usecase.DeleteProductUseCase;
import com.myBusiness.application.usecase.GetProductUseCase;
import com.myBusiness.application.usecase.ListProductsPaginatedUseCase;
import com.myBusiness.application.usecase.UpdateProductUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final CreateProductUseCase createUseCase;
    private final GetProductUseCase getUseCase;
    private final UpdateProductUseCase updateUseCase;
    private final DeleteProductUseCase deleteUseCase;
    private final ListProductsPaginatedUseCase paginatedUseCase;

    /**
     * Crea un producto nuevo. Valida el DTO con @Valid.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductOutputDto> create(
            @RequestBody @Valid ProductInputDto input
    ) {
        ProductOutputDto out = createUseCase.execute(input);
        URI location = URI.create("/api/products/" + out.getId());
        return ResponseEntity.created(location).body(out);
    }

    /**
     * Obtiene un producto por su ID.
     */
    @GetMapping(path = "/{id}")
    public ResponseEntity<ProductOutputDto> getById(@PathVariable("id") Long id) {
        ProductOutputDto out = getUseCase.execute(id);
        return ResponseEntity.ok(out);
    }

    /**
     * GET /api/products/all → devuelve todos los productos (sin paginación).
     */
    @GetMapping("/all")
    public ResponseEntity<List<ProductOutputDto>> getAll() {
        List<ProductOutputDto> list = paginatedUseCase.listAll();
        return ResponseEntity.ok(list);
    }

    /**
     * Listado paginado: GET /api/products?page=0&size=20
     */
    @GetMapping
    public ResponseEntity<PageResponseDto<ProductOutputDto>> listPaginated(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDto<ProductOutputDto> dto = paginatedUseCase.execute(pageable);
        return ResponseEntity.ok(dto);
    }

    /**
     * Actualiza un producto existente.
     */
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductOutputDto> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid ProductInputDto input
    ) {
        ProductOutputDto updated = updateUseCase.execute(id, input);
        return ResponseEntity.ok(updated);
    }

    /**
     * Elimina un producto por su ID.
     */
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
