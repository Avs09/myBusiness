// src/main/java/com/myBusiness/application/usecase/UpdateUnitUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.UnitInputDto;
import com.myBusiness.application.dto.UnitOutputDto;
import com.myBusiness.application.exception.InvalidUnitException;
import com.myBusiness.application.exception.UnitNotFoundException;
import com.myBusiness.domain.model.Unit;
import com.myBusiness.domain.port.UnitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUnitUseCase {
    private final UnitRepository unitRepository;

    @Transactional
    public UnitOutputDto execute(Long id, UnitInputDto input) {
        Unit u = unitRepository.findById(id)
                .orElseThrow(() -> new UnitNotFoundException(id));
        String name = input.getName().trim();
        if (name.isEmpty()) {
            throw new InvalidUnitException("Unit name cannot be empty");
        }
        u.setName(name);
        Unit updated = unitRepository.save(u);
        return UnitOutputDto.builder()
                .id(updated.getId())
                .name(updated.getName())
                .build();
    }
}
