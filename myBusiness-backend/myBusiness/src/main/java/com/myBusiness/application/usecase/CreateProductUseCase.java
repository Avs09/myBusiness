package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.ProductInputDto;
import com.myBusiness.application.dto.ProductOutputDto;
import com.myBusiness.application.exception.InvalidProductException;
import com.myBusiness.application.exception.CategoryNotFoundException;
import com.myBusiness.application.exception.UnitNotFoundException;
import com.myBusiness.domain.model.Product;
import com.myBusiness.domain.port.ProductRepository;
import com.myBusiness.domain.port.CategoryRepository;
import com.myBusiness.domain.port.UnitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CreateProductUseCase {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UnitRepository unitRepository;

    @Transactional
    public ProductOutputDto execute(ProductInputDto input) {
        // 1. Validar thresholds
        if (input.getThresholdMin() > input.getThresholdMax()) {
            throw new InvalidProductException("Threshold minimum cannot be greater than threshold maximum.");
        }

        // 2. Cargar category y unit
        var category = categoryRepository.findById(input.getCategoryId())
            .orElseThrow(() -> new CategoryNotFoundException(input.getCategoryId()));
        var unit = unitRepository.findById(input.getUnitId())
            .orElseThrow(() -> new UnitNotFoundException(input.getUnitId()));
        

        // 3. Construir y guardar entidad
        Product product = Product.builder()
                .name(input.getName())
                .thresholdMin(input.getThresholdMin())
                .thresholdMax(input.getThresholdMax())
                .price(input.getPrice())
                .category(category)
                .unit(unit)
                .build();

        Product saved = productRepository.save(product);

        // 4. Mapear a DTO de salida
        return ProductOutputDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .thresholdMin(saved.getThresholdMin())
                .thresholdMax(saved.getThresholdMax())
                .price(saved.getPrice())
                .categoryName(saved.getCategory().getName())
                .unitName(saved.getUnit().getName())
                .createdDate(saved.getCreatedDate())
                .createdBy(saved.getCreatedBy())
                .modifiedBy(saved.getModifiedBy())
                .modifiedDate(saved.getModifiedDate())
                .build();
    }
    

}
