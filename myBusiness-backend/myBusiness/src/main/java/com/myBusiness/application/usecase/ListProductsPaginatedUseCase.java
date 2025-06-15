package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.PageResponseDto;
import com.myBusiness.application.dto.ProductOutputDto;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ListProductsPaginatedUseCase {

    private final ProductRepository productRepository;
    private final ComputeStockUseCase computeStockUseCase;

    /**
     * Listado paginado de productos con cálculo de stock actual.
     */
    public PageResponseDto<ProductOutputDto> execute(Pageable pageable) {
        Page<Product> page = productRepository.findAll(pageable);

        List<ProductOutputDto> dtoList = page.stream().map(p -> {
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
                    .createdBy(p.getCreatedBy())
                    .createdDate(p.getCreatedDate())
                    .modifiedBy(p.getModifiedBy())
                    .modifiedDate(p.getModifiedDate())
                    .build();
        }).collect(Collectors.toList());

        return PageResponseDto.<ProductOutputDto>builder()
                .content(dtoList)
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    /**
     * Listar todos sin paginar.
     */
    public List<ProductOutputDto> listAll() {
        List<Product> all = productRepository.findAll();
        return all.stream().map(p -> {
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
                    .createdBy(p.getCreatedBy())
                    .createdDate(p.getCreatedDate())
                    .modifiedBy(p.getModifiedBy())
                    .modifiedDate(p.getModifiedDate())
                    .build();
        }).collect(Collectors.toList());
    }
}
