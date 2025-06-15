// src/test/java/com/myBusiness/application/usecase/CreateProductUseCaseTest.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.ProductInputDto;
import com.myBusiness.application.dto.ProductOutputDto;
import com.myBusiness.application.exception.CategoryNotFoundException;
import com.myBusiness.application.exception.UnitNotFoundException;
import com.myBusiness.domain.model.Category;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.model.Unit;
import com.myBusiness.domain.port.CategoryRepository;
import com.myBusiness.domain.port.ProductRepository;
import com.myBusiness.domain.port.UnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateProductUseCaseTest {
    @Mock private ProductRepository productRepo;
    @Mock private CategoryRepository categoryRepo;
    @Mock private UnitRepository unitRepo;
    private CreateProductUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new CreateProductUseCase(productRepo, categoryRepo, unitRepo);
    }

    @Test
    void executeSavesAndReturnsDto() {
        ProductInputDto input = ProductInputDto.builder()
            .name("New Product")
            .thresholdMin(1)
            .thresholdMax(5)
            .price(BigDecimal.valueOf(9.99))
            .categoryId(1L)
            .unitId(2L)
            .build();

        Category category = Category.builder().id(1L).name("Category 1").build();
        Unit unit = Unit.builder().id(2L).name("Unit 1").build();

        when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
        when(unitRepo.findById(2L)).thenReturn(Optional.of(unit));

        Product savedProduct = Product.builder()
            .id(10L)
            .name(input.getName())
            .thresholdMin(input.getThresholdMin())
            .thresholdMax(input.getThresholdMax())
            .price(input.getPrice())
            .category(category)
            .unit(unit)
            .createdDate(Instant.now())
            .createdBy("tester")
            .modifiedDate(Instant.now())
            .modifiedBy("tester")
            .build();

        when(productRepo.save(any(Product.class))).thenReturn(savedProduct);

        ProductOutputDto result = useCase.execute(input);

        assertEquals(10L, result.getId());
        assertEquals("New Product", result.getName());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepo).save(captor.capture());
        assertEquals(input.getName(), captor.getValue().getName());
    }

    @Test
    void executeThrowsCategoryNotFoundException() {
        ProductInputDto input = ProductInputDto.builder()
            .name("Product")
            .thresholdMin(1)
            .thresholdMax(5)
            .price(BigDecimal.valueOf(10))
            .categoryId(99L)
            .unitId(2L)
            .build();

        when(categoryRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> useCase.execute(input));
    }

    @Test
    void executeThrowsUnitNotFoundException() {
        ProductInputDto input = ProductInputDto.builder()
            .name("Product")
            .thresholdMin(1)
            .thresholdMax(5)
            .price(BigDecimal.valueOf(10))
            .categoryId(1L)
            .unitId(99L)
            .build();

        when(categoryRepo.findById(1L)).thenReturn(Optional.of(new Category()));
        when(unitRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UnitNotFoundException.class, () -> useCase.execute(input));
    }
}
