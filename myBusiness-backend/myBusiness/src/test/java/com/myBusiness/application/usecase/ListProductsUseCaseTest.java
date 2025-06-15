// src/test/java/com/myBusiness/application/usecase/ListProductsUseCaseTest.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.ProductOutputDto;
import com.myBusiness.domain.model.Category;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.model.Unit;
import com.myBusiness.domain.port.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListProductsUseCaseTest {
    @Mock
    private ProductRepository productRepository;
    private ListProductsUseCase useCase;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new ListProductsUseCase(productRepository);
    }

    @Test
    void executeReturnsEmptyListWhenNoProducts() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());
        List<ProductOutputDto> result = useCase.execute();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository).findAll();
    }

    @Test
    void executeMapsProductToDto() {
        // Preparar producto con categoría y unidad válidas
        Category cat = Category.builder().id(1L).name("Cat").build();
        Unit unit = Unit.builder().id(2L).name("U").build();
        Product p = Product.builder()
            .id(1L)
            .name("Test")
            .thresholdMin(10)
            .thresholdMax(100)
            .price(BigDecimal.valueOf(5.5))
            .category(cat)
            .unit(unit)
            .createdDate(Instant.parse("2025-01-01T00:00:00Z"))
            .createdBy("user")
            .modifiedDate(Instant.parse("2025-01-02T00:00:00Z"))
            .modifiedBy("user2")
            .build();
        when(productRepository.findAll()).thenReturn(List.of(p));

        List<ProductOutputDto> dtos = useCase.execute();

        assertEquals(1, dtos.size());
        ProductOutputDto dto = dtos.get(0);
        assertEquals(1L, dto.getId());
        assertEquals("Test", dto.getName());
        assertEquals(10, dto.getThresholdMin());
        assertEquals(100, dto.getThresholdMax());
        assertEquals(BigDecimal.valueOf(5.5), dto.getPrice());
        assertEquals("Cat", dto.getCategoryName());
        assertEquals("U", dto.getUnitName());
        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), dto.getCreatedDate());
        assertEquals("user", dto.getCreatedBy());
        verify(productRepository).findAll();
    }
}
