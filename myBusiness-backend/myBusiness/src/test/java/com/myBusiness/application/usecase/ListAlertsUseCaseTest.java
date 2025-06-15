// src/test/java/com/myBusiness/application/usecase/ListAlertsUseCaseTest.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.AlertOutputDto;
import com.myBusiness.domain.model.Alert;
import com.myBusiness.domain.model.AlertType;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.port.AlertRepository;
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

class ListAlertsUseCaseTest {

    @Mock
    private AlertRepository alertRepo;

    private ListAlertsUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new ListAlertsUseCase(alertRepo);
    }

    @Test
    void executeReturnsEmptyListWhenNoUnread() {
        when(alertRepo.findAllUnread()).thenReturn(Collections.emptyList());

        List<AlertOutputDto> result = useCase.execute();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(alertRepo).findAllUnread();
    }

    @Test
    void executeMapsAlertsToDto() {
        // Entidad Product
        Product prod = Product.builder()
            .id(1L)
            .name("Prod1")
            .thresholdMin(0)
            .thresholdMax(10)
            .price(BigDecimal.ONE)
            .build();

        // Creamos la alerta sin movimiento (no se mapea)
        Alert alert = Alert.builder()
            .id(3L)
            .product(prod)
            .movement(null)  // omitimos el movimiento
            .alertType(AlertType.UNDERSTOCK)
            .triggeredAt(Instant.parse("2025-05-01T00:00:00Z"))
            .isRead(false)
            .createdBy("u")
            .createdDate(Instant.parse("2025-05-02T00:00:00Z"))
            .modifiedBy("u")
            .modifiedDate(Instant.parse("2025-05-03T00:00:00Z"))
            .build();

        when(alertRepo.findAllUnread()).thenReturn(List.of(alert));

        List<AlertOutputDto> dtos = useCase.execute();

        assertEquals(1, dtos.size());
        AlertOutputDto dto = dtos.get(0);
        assertEquals(3L, dto.getId());
        assertEquals(1L, dto.getProductId());
        assertEquals("UNDERSTOCK", dto.getAlertType());
        assertFalse(dto.isRead());
        assertEquals("u", dto.getCreatedBy());
        assertEquals(Instant.parse("2025-05-02T00:00:00Z"), dto.getCreatedDate());
        assertEquals("u", dto.getModifiedBy());
        assertEquals(Instant.parse("2025-05-03T00:00:00Z"), dto.getModifiedDate());

        verify(alertRepo).findAllUnread();
    }
}
