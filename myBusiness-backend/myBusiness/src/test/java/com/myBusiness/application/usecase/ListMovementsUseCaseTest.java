// src/test/java/com/myBusiness/application/usecase/ListMovementsUseCaseTest.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.MovementOutputDto;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.model.MovementType;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.port.InventoryMovementRepository;
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

class ListMovementsUseCaseTest {

    @Mock private InventoryMovementRepository movRepo;
    private ListMovementsUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new ListMovementsUseCase(movRepo);
    }

    @Test
    void executeReturnsEmptyListWhenNoMovements() {
        when(movRepo.findAllByProductId(1L)).thenReturn(Collections.emptyList());

        List<MovementOutputDto> result = useCase.execute(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void executeMapsMovementsToDto() {
        Product prod = Product.builder()
            .id(1L)
            .name("P")
            .thresholdMin(0)
            .thresholdMax(10)
            .price(BigDecimal.ONE)
            .build();

        InventoryMovement mov = InventoryMovement.builder()
            .id(2L)
            .product(prod)
            .movementType(MovementType.ENTRY)
            .quantity(BigDecimal.valueOf(5))
            .reason("Test")
            .movementDate(Instant.parse("2025-05-01T00:00:00Z"))
            .createdBy("u")
            .createdDate(Instant.parse("2025-05-02T00:00:00Z"))
            .modifiedBy("u2")
            .modifiedDate(Instant.parse("2025-05-03T00:00:00Z"))
            .build();
        when(movRepo.findAllByProductId(1L)).thenReturn(List.of(mov));

        List<MovementOutputDto> dtos = useCase.execute(1L);

        assertEquals(1, dtos.size());
        MovementOutputDto dto = dtos.get(0);
        assertEquals(2L, dto.getId());
        assertEquals(1L, dto.getProductId());
        assertEquals("ENTRY", dto.getMovementType());
        assertEquals(5.0, dto.getQuantity().doubleValue());
        assertEquals("Test", dto.getReason());
        assertEquals(Instant.parse("2025-05-02T00:00:00Z"), dto.getCreatedDate());
        assertEquals("u", dto.getCreatedBy());
        assertEquals(Instant.parse("2025-05-03T00:00:00Z"), dto.getModifiedDate());
        assertEquals("u2", dto.getModifiedBy());
    }
}
