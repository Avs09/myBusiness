// src/main/java/com/myBusiness/application/usecase/ListCustomFieldsUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.CustomFieldOutputDto;
import com.myBusiness.domain.port.CustomFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListCustomFieldsUseCase {
    private final CustomFieldRepository fieldRepo;

    public List<CustomFieldOutputDto> execute(Long productId) {
        return fieldRepo.findAllByProductId(productId).stream()
            .map(f -> CustomFieldOutputDto.builder()
                .id(f.getId())
                .productId(f.getProductId())
                .name(f.getName())
                .dataType(f.getDataType())
                .createdBy(f.getCreatedBy())
                .createdDate(f.getCreatedDate())
                .modifiedBy(f.getModifiedBy())
                .modifiedDate(f.getModifiedDate())
                .build())
            .collect(Collectors.toList());
    }
}
