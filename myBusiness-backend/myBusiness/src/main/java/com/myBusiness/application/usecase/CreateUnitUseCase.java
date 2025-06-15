// src/main/java/com/myBusiness/application/usecase/CreateUnitUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.UnitInputDto;
import com.myBusiness.application.dto.UnitOutputDto;
import com.myBusiness.application.exception.InvalidUnitException;
import com.myBusiness.domain.model.Unit;
import com.myBusiness.domain.port.UnitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUnitUseCase {
    private final UnitRepository unitRepository;

    @Transactional
    public UnitOutputDto execute(UnitInputDto input) {
        String name = input.getName().trim();
        if (name.isEmpty()) {
            throw new InvalidUnitException("Unit name cannot be empty");
        }
        Unit entity = Unit.builder()
                .name(name)
                .build();
        Unit saved = unitRepository.save(entity);
        return UnitOutputDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .build();
    }
}
