// src/main/java/com/myBusiness/application/usecase/GetUnitUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.UnitOutputDto;
import com.myBusiness.application.exception.UnitNotFoundException;
import com.myBusiness.domain.model.Unit;
import com.myBusiness.domain.port.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUnitUseCase {
    private final UnitRepository unitRepository;

    public UnitOutputDto execute(Long id) {
        Unit u = unitRepository.findById(id)
                .orElseThrow(() -> new UnitNotFoundException(id));
        return UnitOutputDto.builder()
                .id(u.getId())
                .name(u.getName())
                .build();
    }
}
