// src/main/java/com/myBusiness/application/usecase/ImportProductsUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.ProductInputDto;
import com.myBusiness.application.exception.ImportExportException;
import com.myBusiness.application.util.CsvUtils;
import com.myBusiness.domain.model.Category;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.model.Unit;
import com.myBusiness.domain.port.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImportProductsUseCase {
    private final ProductRepository prodRepo;

    @Transactional
    public void execute(MultipartFile file) {
        try {
            var dtos = CsvUtils.parseProducts(file);
            for (ProductInputDto dto : dtos) {
                Category category = new Category();
                category.setId(dto.getCategoryId());

                Unit unit = new Unit();
                unit.setId(dto.getUnitId());

                Product p = Product.builder()
                    .name(dto.getName())
                    .thresholdMin(dto.getThresholdMin())
                    .thresholdMax(dto.getThresholdMax())
                    .price(dto.getPrice())
                    .category(category) 
                    .unit(unit)          
                    .build();

                prodRepo.save(p);
            }
        } catch (ImportExportException ie) {
            throw ie;
        } catch (Exception e) {
            throw new ImportExportException("Failed to import products", e);
        }
    }
}
