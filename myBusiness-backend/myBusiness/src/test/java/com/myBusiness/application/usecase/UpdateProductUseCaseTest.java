// src/test/java/com/myBusiness/application/usecase/UpdateProductUseCaseTest.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.ProductInputDto;
import com.myBusiness.application.exception.ProductNotFoundException;
import com.myBusiness.domain.model.Category;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.model.Unit;
import com.myBusiness.domain.port.CategoryRepository;
import com.myBusiness.domain.port.ProductRepository;
import com.myBusiness.domain.port.UnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateProductUseCaseTest {

    @Mock private ProductRepository repo;
    @Mock private CategoryRepository categoryRepo;
    @Mock private UnitRepository unitRepo;

    private UpdateProductUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new UpdateProductUseCase(repo, categoryRepo, unitRepo);
    }

    @Test
    void executeThrowsWhenNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> useCase.execute(1L, null));
    }

    @Test
    void executeUpdatesAndReturnsDto() {
        // Preparar producto existente
        Category cat = Category.builder().id(1L).name("Cat").build();
        Unit unit = Unit.builder().id(2L).name("U").build();
        Product p = Product.builder()
            .id(1L)
            .name("A")
            .thresholdMin(1)
            .thresholdMax(5)
            .price(BigDecimal.ONE)
            .category(cat)
            .unit(unit)
            .createdDate(Instant.now())
            .createdBy("u")
            .modifiedDate(Instant.now())
            .modifiedBy("u")
            .build();

        // Mocks de repositorios
        when(repo.findById(1L)).thenReturn(Optional.of(p));
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(cat));
        when(unitRepo.findById(2L)).thenReturn(Optional.of(unit));

        // Datos de entrada
        ProductInputDto input = ProductInputDto.builder()
            .name("B")
            .thresholdMin(2)
            .thresholdMax(6)
            .price(BigDecimal.TEN)
            .categoryId(1L)
            .unitId(2L)
            .build();

        when(repo.save(any(Product.class))).thenReturn(p);

        // Ejecutar
        var dto = useCase.execute(1L, input);

        // Verificar
        assertNotNull(dto);
        assertEquals("B", dto.getName());
        assertEquals(BigDecimal.TEN, dto.getPrice());
        verify(repo).save(p);
    }
}
