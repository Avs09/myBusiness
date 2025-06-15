
package com.myBusiness.application.usecase;
import java.util.stream.Collectors;

import com.myBusiness.application.dto.FieldValueOutputDto;
import com.myBusiness.domain.port.FieldValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;                        // ← IMPORT MUY IMPORTANTE


@Service
@RequiredArgsConstructor
public class ListFieldValuesUseCase {
    private final FieldValueRepository valueRepo;

    public List<FieldValueOutputDto> execute(Long productId, Long fieldId) {
        return valueRepo.findAllByProductIdAndFieldId(productId, fieldId).stream()
        		.map(v -> FieldValueOutputDto.builder()
        			    .id(v.getId())
        			    .productId(v.getProductId())
        			    .fieldId(v.getField().getId())
        			    .valueText(v.getValueText())
        			    .valueNumber(v.getValueNumber())
        			    .valueDate(v.getValueDate())
        			    .createdDate(v.getCreatedDate())    
        			    .createdBy(v.getCreatedBy())
        			    .modifiedDate(v.getModifiedDate()) 
        			    .modifiedBy(v.getModifiedBy())
        			    .build()
        			)

            .collect(Collectors.toList());           
    }
}
