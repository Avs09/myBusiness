// src/test/java/com/myBusiness/application/usecase/ListProductsPaginatedUseCaseTest.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.PageResponseDto;
import com.myBusiness.application.dto.ProductOutputDto;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.port.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListProductsPaginatedUseCaseTest {

    @Mock
    private ProductRepository repo;

    private ListProductsPaginatedUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new ListProductsPaginatedUseCase(repo);
    }

    @Test
    void executeReturnsPageDto() {
        Pageable pg = PageRequest.of(0, 1);

        // Crear un Product con categoría y unidad válidas:
        Product p = Product.builder()
            .id(1L)
            .name("Product 1")
            .thresholdMin(1)
            .thresholdMax(10)
            .price(BigDecimal.TEN)
            .category(new com.myBusiness.domain.model.Category(1L, "Cat"))
            .unit(new com.myBusiness.domain.model.Unit(2L, "U"))
            .createdDate(Instant.now())
            .createdBy("u")
            .modifiedDate(Instant.now())
            .modifiedBy("u")
            .build();

        Page<Product> page = new PageImpl<>(List.of(p), pg, 1);
        when(repo.findAll(pg)).thenReturn(page);

        PageResponseDto<ProductOutputDto> resp = useCase.execute(pg);

        assertEquals(1L, resp.getTotalElements());
        assertEquals(1, resp.getContent().size());
        ProductOutputDto dto = resp.getContent().get(0);
        assertEquals("Product 1", dto.getName());
        assertEquals("Cat", dto.getCategoryName());
        assertEquals("U", dto.getUnitName());
        assertEquals(0, resp.getPage());
        assertEquals(1, resp.getSize());
        assertEquals(1, resp.getTotalPages());
    }

}
