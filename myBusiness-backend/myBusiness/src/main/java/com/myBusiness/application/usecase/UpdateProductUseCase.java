package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.ProductInputDto;
import com.myBusiness.application.dto.ProductOutputDto;
import com.myBusiness.application.exception.InvalidProductException;
import com.myBusiness.application.exception.ProductNotFoundException;
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
public class UpdateProductUseCase {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UnitRepository unitRepository;

    @Transactional
    public ProductOutputDto execute(Long productId, ProductInputDto input) {
        // 1. Cargar producto
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        // 2. Validaciones de thresholds
        if (input.getThresholdMin() > input.getThresholdMax()) {
            throw new InvalidProductException("Threshold minimum cannot be greater than threshold maximum.");
        }

        // 3. Cargar categoría y unidad
        var category = categoryRepository.findById(input.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(input.getCategoryId()));
        var unit = unitRepository.findById(input.getUnitId())
                .orElseThrow(() -> new UnitNotFoundException(input.getUnitId()));

        // 4. Aplicar cambios via métodos de dominio
        product.updateName(input.getName());
        product.updateThresholds(input.getThresholdMin(), input.getThresholdMax());
        product.updatePrice(input.getPrice());
        product.setCategory(category); 
        product.setUnit(unit); 
        
        // 5. Guardar
        Product updated = productRepository.save(product);

        // 6. Mapear salida
        return ProductOutputDto.builder()
                .id(updated.getId())
                .name(updated.getName())
                .thresholdMin(updated.getThresholdMin())
                .thresholdMax(updated.getThresholdMax())
                .price(updated.getPrice())
                .categoryName(updated.getCategory().getName())
                .unitName(updated.getUnit().getName())
                .createdBy(updated.getCreatedBy())
                .createdDate(updated.getCreatedDate())
                .modifiedBy(updated.getModifiedBy())
                .modifiedDate(updated.getModifiedDate())
                .build();
    }
}
