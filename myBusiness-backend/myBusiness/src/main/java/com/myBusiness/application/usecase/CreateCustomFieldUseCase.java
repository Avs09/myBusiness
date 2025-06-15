// src/main/java/com/myBusiness/application/usecase/CreateCustomFieldUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.CustomFieldInputDto;
import com.myBusiness.application.dto.CustomFieldOutputDto;
import com.myBusiness.application.exception.InvalidProductException;
import com.myBusiness.domain.model.CustomField;
import com.myBusiness.domain.port.CustomFieldRepository;
import com.myBusiness.domain.port.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCustomFieldUseCase {
    private final CustomFieldRepository fieldRepo;
    private final ProductRepository productRepo;

    @Transactional
    public CustomFieldOutputDto execute(CustomFieldInputDto input) {
        productRepo.findById(input.getProductId())
            .orElseThrow(() -> new InvalidProductException("Product not found: " + input.getProductId()));

        CustomField field = CustomField.builder()
            .productId(input.getProductId())
            .name(input.getName().trim())
            .dataType(input.getDataType().trim())
            .build();
        CustomField saved = fieldRepo.save(field);

        return CustomFieldOutputDto.builder()
            .id(saved.getId())
            .productId(saved.getProductId())
            .name(saved.getName())
            .dataType(saved.getDataType())
            .createdBy(saved.getCreatedBy())
            .createdDate(saved.getCreatedDate())
            .modifiedBy(saved.getModifiedBy())
            .modifiedDate(saved.getModifiedDate())
            .build();
    }
}
