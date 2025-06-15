// src/test/java/com/myBusiness/application/usecase/ExportProductsUseCaseTest.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.ProductInputDto;
import com.myBusiness.application.exception.ImportExportException;
import com.myBusiness.application.util.CsvUtils;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.port.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ImportProductsUseCaseTest {

@Mock private ProductRepository productRepo;
@Mock private MultipartFile file;
private MockedStatic<CsvUtils> csvMock;
private ImportProductsUseCase useCase;

@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
    csvMock = mockStatic(CsvUtils.class);
    useCase = new ImportProductsUseCase(productRepo);
}

@AfterEach
void tearDown() {
    csvMock.close();
}

@Test
void executeImportsSuccessfully() throws Exception {
    // Preparar DTOs simulados
    ProductInputDto dto = ProductInputDto.builder()
        .name("Imp")
        .thresholdMin(1)
        .thresholdMax(2)
        .price(BigDecimal.ONE)
        .categoryId(1L)
        .unitId(2L)
        .build();
    csvMock.when(() -> CsvUtils.parseProducts(file)).thenReturn(List.of(dto));

    // Simular guardado
    Product p = Product.builder()
        .id(10L)
        .name(dto.getName())
        .thresholdMin(dto.getThresholdMin())
        .thresholdMax(dto.getThresholdMax())
        .price(dto.getPrice())
        .createdDate(Instant.now())
        .createdBy("u")
        .modifiedDate(Instant.now())
        .modifiedBy("u")
        .build();
    when(productRepo.save(any(Product.class))).thenReturn(p);

    assertDoesNotThrow(() -> useCase.execute(file));
    verify(productRepo).save(any(Product.class));
}

@Test
void executeThrowsWhenParsingError() throws Exception {
    csvMock.when(() -> CsvUtils.parseProducts(file))
           .thenThrow(new ImportExportException("fail", null));
    assertThrows(ImportExportException.class, () -> useCase.execute(file));
}

}
