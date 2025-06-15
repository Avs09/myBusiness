// src/main/java/com/myBusiness/application/usecase/ExportProductsUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.ProductOutputDto;
import com.myBusiness.application.exception.ImportExportException;
import com.myBusiness.application.util.CsvUtils;
import com.myBusiness.application.util.ExcelUtils;
import com.myBusiness.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportProductsUseCase {
    private final ProductRepository prodRepo;

    public byte[] toCsv() {
        try {
        	List<ProductOutputDto> dtos = prodRepo.findAll().stream()
        		    .map(p -> ProductOutputDto.builder()
        		        .id(p.getId())
        		        .name(p.getName())
        		        .thresholdMin(p.getThresholdMin())
        		        .thresholdMax(p.getThresholdMax())
        		        .price(p.getPrice())
        		        .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
        		        .unitName(p.getUnit() != null ? p.getUnit().getName() : null)
        		        .createdDate(p.getCreatedDate()) 
        		        .createdBy(p.getCreatedBy())
        		        .modifiedDate(p.getModifiedDate())
        		        .modifiedBy(p.getModifiedBy())
        		        .build()
        		    )
        		    .collect(Collectors.toList());

            return CsvUtils.toCsv(dtos);
        } catch (Exception e) {
            throw new ImportExportException("Failed to export CSV", e);
        }
    }

    public byte[] toExcel() {
        try {
            List<ProductOutputDto> dtos = prodRepo.findAll().stream()
                .map(p -> ProductOutputDto.builder()
                    .id(p.getId())
                    .name(p.getName())
                    .thresholdMin(p.getThresholdMin())
                    .thresholdMax(p.getThresholdMax())
                    .price(p.getPrice())
                    .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                    .unitName(p.getUnit() != null ? p.getUnit().getName() : null)
                    .createdDate(p.getCreatedDate())
                    .createdBy(p.getCreatedBy())
                    .modifiedDate(p.getModifiedDate())
                    .modifiedBy(p.getModifiedBy())
                    .build()
                )
                .collect(Collectors.toList());

            return ExcelUtils.toExcel(dtos);  

        } catch (Exception e) {
            throw new ImportExportException("Failed to export Excel", e);
        }
    }

}
