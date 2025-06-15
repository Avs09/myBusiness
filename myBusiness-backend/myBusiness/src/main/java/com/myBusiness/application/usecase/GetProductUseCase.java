package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.ProductOutputDto;
import com.myBusiness.application.exception.ProductNotFoundException;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class GetProductUseCase {

    private final ProductRepository productRepository;
    private final ComputeStockUseCase computeStockUseCase;

    public ProductOutputDto execute(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        BigDecimal stock = computeStockUseCase.execute(productId);
        return ProductOutputDto.builder()
                .id(product.getId())
                .name(product.getName())
                .thresholdMin(product.getThresholdMin())
                .thresholdMax(product.getThresholdMax())
                .price(product.getPrice())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .unitId(product.getUnit().getId())
                .unitName(product.getUnit().getName())
                .currentStock(stock)
                .createdDate(product.getCreatedDate())
                .createdBy(product.getCreatedBy())
                .modifiedDate(product.getModifiedDate())
                .modifiedBy(product.getModifiedBy())
                .build();
    }
}
