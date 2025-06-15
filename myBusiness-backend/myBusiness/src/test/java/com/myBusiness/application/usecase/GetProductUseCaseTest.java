// src/test/java/com/myBusiness/application/usecase/GetProductUseCaseTest.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.ProductOutputDto;
import com.myBusiness.application.exception.ProductNotFoundException;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.port.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetProductUseCaseTest {
    @Mock
    private ProductRepository repo;
    private GetProductUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new GetProductUseCase(repo);
    }

    @Test
    void executeReturnsDtoWhenFound() {
        Product p = Product.builder()
            .id(1L)
            .name("TestProd")
            .thresholdMin(0)
            .thresholdMax(10)
            .price(BigDecimal.valueOf(5.5))
            // Proporciona una categoría y unidad válidas
            .category(new com.myBusiness.domain.model.Category(1L, "Cat"))
            .unit(new com.myBusiness.domain.model.Unit(2L, "Unit"))
            .createdDate(Instant.now())
            .createdBy("tester")
            .modifiedDate(Instant.now())
            .modifiedBy("tester")
            .build();
        when(repo.findById(1L)).thenReturn(Optional.of(p));

        ProductOutputDto dto = useCase.execute(1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("TestProd", dto.getName());
        verify(repo).findById(1L);
    }

    @Test
    void executeThrowsProductNotFoundExceptionWhenNotFound() {
        when(repo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> useCase.execute(99L));
    }
}
