// src/test/java/com/myBusiness/application/usecase/MarkAlertReadUseCaseTest.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.AlertOutputDto;
import com.myBusiness.application.exception.AlertNotFoundException;
import com.myBusiness.domain.model.Alert;
import com.myBusiness.domain.model.AlertType;
import com.myBusiness.domain.model.Category;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.model.Unit;
import com.myBusiness.domain.port.AlertRepository;
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

class MarkAlertReadUseCaseTest {

    @Mock private AlertRepository alertRepo;
    private MarkAlertReadUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new MarkAlertReadUseCase(alertRepo);
    }

    @Test
    void executeThrowsWhenNotFound() {
        when(alertRepo.findById(5L)).thenReturn(Optional.empty());
        assertThrows(AlertNotFoundException.class, () -> useCase.execute(5L));
    }

    @Test
    void executeMarksAsReadAndReturnsDto() {
        // Preparar una alerta con producto y tipo válidos
        Product prod = Product.builder()
            .id(1L)
            .name("Prod")
            .thresholdMin(0)
            .thresholdMax(100)
            .price(BigDecimal.ZERO)
            .category(Category.builder().id(1L).name("Cat").build())
            .unit(Unit.builder().id(2L).name("U").build())
            .build();

        Alert alert = Alert.builder()
            .id(1L)
            .product(prod)
            .movement(null)
            .alertType(AlertType.UNDERSTOCK)
            .triggeredAt(Instant.now())
            .isRead(false)
            .createdBy("u")
            .createdDate(Instant.now())
            .modifiedBy("u")
            .modifiedDate(Instant.now())
            .build();
        when(alertRepo.findById(1L)).thenReturn(Optional.of(alert));
        when(alertRepo.save(any(Alert.class))).thenReturn(alert);

        // Ejecutar caso de uso
        AlertOutputDto output = useCase.execute(1L);

        // Verificar DTO resultante y persistencia
        assertTrue(output.isRead());
        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepo).save(captor.capture());
        assertTrue(captor.getValue().isRead());
    }
}
