// src/main/java/com/myBusiness/application/usecase/UpdateCustomFieldUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.CustomFieldInputDto;
import com.myBusiness.application.dto.CustomFieldOutputDto;
import com.myBusiness.application.exception.CustomFieldNotFoundException;
import com.myBusiness.domain.model.CustomField;
import com.myBusiness.domain.port.CustomFieldRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateCustomFieldUseCase {
    private final CustomFieldRepository fieldRepo;

    @Transactional
    public CustomFieldOutputDto execute(Long fieldId, CustomFieldInputDto input) {
        CustomField existing = fieldRepo.findById(fieldId)
            .orElseThrow(() -> new CustomFieldNotFoundException(fieldId));

        existing.setName(input.getName().trim());
        existing.setDataType(input.getDataType().trim());
        CustomField updated = fieldRepo.save(existing);

        return CustomFieldOutputDto.builder()
            .id(updated.getId())
            .productId(updated.getProductId())
            .name(updated.getName())
            .dataType(updated.getDataType())
            .createdBy(updated.getCreatedBy())
            .createdDate(updated.getCreatedDate())
            .modifiedBy(updated.getModifiedBy())
            .modifiedDate(updated.getModifiedDate())
            .build();
    }
}
