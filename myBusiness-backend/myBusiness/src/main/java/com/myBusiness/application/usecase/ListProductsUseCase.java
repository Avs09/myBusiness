package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.ProductOutputDto;

import com.myBusiness.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ListProductsUseCase {

    private final ProductRepository productRepository;
    private final ComputeStockUseCase computeStockUseCase;

    /**
     * Listar todos los productos con cálculo de stock actual vía ComputeStockUseCase.
     */
    public List<ProductOutputDto> execute() {
        return productRepository.findAll().stream()
            .map(p -> {
                BigDecimal stock = computeStockUseCase.execute(p.getId());
                return ProductOutputDto.builder()
                    .id(p.getId())
                    .name(p.getName())
                    .thresholdMin(p.getThresholdMin())
                    .thresholdMax(p.getThresholdMax())
                    .price(p.getPrice())
                    .categoryId(p.getCategory().getId())
                    .categoryName(p.getCategory().getName())
                    .unitId(p.getUnit().getId())
                    .unitName(p.getUnit().getName())
                    .currentStock(stock)
                    .createdDate(p.getCreatedDate())
                    .createdBy(p.getCreatedBy())
                    .modifiedDate(p.getModifiedDate())
                    .modifiedBy(p.getModifiedBy())
                    .build();
            })
            .collect(Collectors.toList());
    }
}
