// src/main/java/com/myBusiness/application/usecase/CreateFieldValueUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.FieldValueInputDto;
import com.myBusiness.application.dto.FieldValueOutputDto;
import com.myBusiness.application.exception.CustomFieldNotFoundException;
import com.myBusiness.domain.model.CustomField;
import com.myBusiness.domain.model.FieldValue;
import com.myBusiness.domain.port.CustomFieldRepository;
import com.myBusiness.domain.port.FieldValueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CreateFieldValueUseCase {
    private final FieldValueRepository valueRepo;
    private final CustomFieldRepository fieldRepo;

    @Transactional
    public FieldValueOutputDto execute(FieldValueInputDto input) {
        CustomField field = fieldRepo.findById(input.getFieldId())
            .orElseThrow(() -> new CustomFieldNotFoundException(input.getFieldId()));

        FieldValue v = FieldValue.builder()
            .productId(input.getProductId())
            .field(field)
            .valueText(input.getValueText())
            .valueNumber(input.getValueNumber())
            .valueDate(input.getValueDate())
            .createdDate(Instant.now())
            .build();
        FieldValue saved = valueRepo.save(v);

        return FieldValueOutputDto.builder()
            .id(saved.getId())
            .productId(saved.getProductId())
            .fieldId(saved.getField().getId())
            .valueText(saved.getValueText())
            .valueNumber(saved.getValueNumber())
            .valueDate(saved.getValueDate())
            .createdBy(saved.getCreatedBy())
            .createdDate(saved.getCreatedDate())
            .modifiedBy(saved.getModifiedBy())
            .modifiedDate(saved.getModifiedDate())
            .build();
    }
}
