// src/test/java/com/myBusiness/application/usecase/GenerateInventoryReportUseCaseTest.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.InventoryReportRowDto;
import com.myBusiness.application.dto.ReportFilterDto;
import com.myBusiness.domain.model.Category;
import com.myBusiness.domain.model.InventoryMovement;
import com.myBusiness.domain.model.MovementType;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.model.Unit;
import com.myBusiness.domain.port.InventoryMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenerateInventoryReportUseCaseTest {

    @Mock
    private InventoryMovementRepository movementRepo;
    private GenerateInventoryReportUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new GenerateInventoryReportUseCase(movementRepo);
    }

    @Test
    void executeGroupsMovementsCorrectly() {
    	Product prod = Product.builder()
    		    .id(1L)
    		    .name("P")
    		    .thresholdMin(0)
    		    .thresholdMax(100)
    		    .price(BigDecimal.ZERO)
    		    .category(new Category(1L, "Cat"))
    		    .unit(new Unit(1L, "U"))
    		    .build();

        InventoryMovement m1 = InventoryMovement.builder()
            .id(1L)
            .product(prod)
            .movementType(MovementType.ENTRY)
            .quantity(BigDecimal.valueOf(10))
            .movementDate(Instant.parse("2025-05-01T00:00:00Z"))
            .build();
        InventoryMovement m2 = InventoryMovement.builder()
            .id(2L)
            .product(prod)
            .movementType(MovementType.EXIT)
            .quantity(BigDecimal.valueOf(3))
            .movementDate(Instant.parse("2025-05-02T00:00:00Z"))
            .build();

        ReportFilterDto filter = new ReportFilterDto();
        filter.setProductId(1L);
        filter.setCategoryId(null);
        filter.setUnitId(null);
        filter.setDateFrom(LocalDate.of(2025, 5, 1));
        filter.setDateTo(LocalDate.of(2025, 5, 31));

        when(movementRepo.findByFilter(1L, null, null, filter.getDateFrom(), filter.getDateTo()))
            .thenReturn(List.of(m1, m2));

        List<InventoryReportRowDto> rows = useCase.execute(filter);
        assertEquals(1, rows.size());
        InventoryReportRowDto row = rows.get(0);
        assertEquals(1L, row.getProductId());
        assertEquals("P", row.getProductName());
        assertEquals(BigDecimal.valueOf(7), row.getCurrentStock());
        assertEquals(Instant.parse("2025-05-02T00:00:00Z"), row.getLastMovementDate());
    }

    @Test
    void exportToExcelReturnsBytes() throws Exception {
        ReportFilterDto filter = new ReportFilterDto();
        filter.setProductId(1L);
        filter.setCategoryId(null);
        filter.setUnitId(null);
        filter.setDateFrom(LocalDate.of(2025, 5, 1));
        filter.setDateTo(LocalDate.of(2025, 5, 31));

        when(movementRepo.findByFilter(anyLong(), any(), any(), any(), any()))
            .thenReturn(List.of());

        byte[] data = useCase.exportToExcel(filter);
        assertNotNull(data);
    }

    @Test
    void exportToPdfReturnsBytes() throws Exception {
        ReportFilterDto filter = new ReportFilterDto();
        filter.setProductId(1L);
        filter.setCategoryId(null);
        filter.setUnitId(null);
        filter.setDateFrom(LocalDate.of(2025, 5, 1));
        filter.setDateTo(LocalDate.of(2025, 5, 31));

        when(movementRepo.findByFilter(anyLong(), any(), any(), any(), any()))
            .thenReturn(List.of());

        byte[] data = useCase.exportToPdf(filter);
        assertNotNull(data);
    }
}
