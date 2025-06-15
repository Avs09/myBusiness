// src/test/java/com/myBusiness/application/usecase/ExportProductsUseCaseTest.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.exception.ImportExportException;
import com.myBusiness.application.util.CsvUtils;
import com.myBusiness.application.util.ExcelUtils;
import com.myBusiness.domain.model.Category;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.model.Unit;
import com.myBusiness.domain.port.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class ExportProductsUseCaseTest {

@Mock private ProductRepository productRepo;
private ExportProductsUseCase useCase;

@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
    useCase = new ExportProductsUseCase(productRepo);
}

@Test
void toCsvReturnsBytes() throws Exception {
    // Preparar datos de producto
    Product p = Product.builder()
        .id(1L)
        .name("X")
        .thresholdMin(0)
        .thresholdMax(10)
        .price(BigDecimal.valueOf(5))
        .category(Category.builder().id(1L).name("C").build())
        .unit(Unit.builder().id(2L).name("U").build())
        .createdDate(Instant.now())
        .createdBy("u")
        .modifiedDate(Instant.now())
        .modifiedBy("u")
        .build();
    when(productRepo.findAll()).thenReturn(List.of(p));

    try (MockedStatic<CsvUtils> csvMock = mockStatic(CsvUtils.class)) {
        csvMock.when(() -> CsvUtils.toCsv(anyList())).thenReturn(new byte[]{0x01, 0x02});

        byte[] csv = useCase.toCsv();
        assertArrayEquals(new byte[]{0x01, 0x02}, csv);
        verify(productRepo).findAll();
    }
}

@Test
void toExcelReturnsBytes() throws Exception {
    // Preparar datos de producto
    Product p = Product.builder()
        .id(2L)
        .name("Y")
        .thresholdMin(1)
        .thresholdMax(5)
        .price(BigDecimal.valueOf(3))
        .category(Category.builder().id(1L).name("C").build())
        .unit(Unit.builder().id(2L).name("U").build())
        .createdDate(Instant.now())
        .createdBy("u")
        .modifiedDate(Instant.now())
        .modifiedBy("u")
        .build();
    when(productRepo.findAll()).thenReturn(List.of(p));

    try (MockedStatic<ExcelUtils> excelMock = mockStatic(ExcelUtils.class)) {
        excelMock.when(() -> ExcelUtils.toExcel(anyList())).thenReturn(new byte[]{0x0A, 0x0B});

        byte[] xlsx = useCase.toExcel();
        assertArrayEquals(new byte[]{0x0A, 0x0B}, xlsx);
        verify(productRepo).findAll();
    }
}

@Test
void toCsvThrowsImportExportException() {
    when(productRepo.findAll()).thenThrow(new RuntimeException("db fail"));
    assertThrows(ImportExportException.class, () -> useCase.toCsv());
}

@Test
void toExcelThrowsImportExportException() {
    when(productRepo.findAll()).thenThrow(new RuntimeException("db fail"));
    assertThrows(ImportExportException.class, () -> useCase.toExcel());
}


}
