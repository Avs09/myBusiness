// src/main/java/com/myBusiness/application/usecase/UpdateFieldValueUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.FieldValueInputDto;
import com.myBusiness.application.dto.FieldValueOutputDto;
import com.myBusiness.application.exception.FieldValueNotFoundException;
import com.myBusiness.domain.model.FieldValue;
import com.myBusiness.domain.port.FieldValueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateFieldValueUseCase {
    private final FieldValueRepository valueRepo;

    @Transactional
    public FieldValueOutputDto execute(Long valueId, FieldValueInputDto input) {
        FieldValue existing = valueRepo.findById(valueId)
            .orElseThrow(() -> new FieldValueNotFoundException(valueId));

        existing.setValueText(input.getValueText());
        existing.setValueNumber(input.getValueNumber());
        existing.setValueDate(input.getValueDate());
        FieldValue updated = valueRepo.save(existing);

        return FieldValueOutputDto.builder()
            .id(updated.getId())
            .productId(updated.getProductId())
            .fieldId(updated.getField().getId())
            .valueText(updated.getValueText())
            .valueNumber(updated.getValueNumber())
            .valueDate(updated.getValueDate())
            .createdBy(updated.getCreatedBy())
            .createdDate(updated.getCreatedDate())
            .modifiedBy(updated.getModifiedBy())
            .modifiedDate(updated.getModifiedDate())
            .build();
    }
}
