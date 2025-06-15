// src/main/java/com/myBusiness/application/usecase/ListUnitsUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.UnitOutputDto;
import com.myBusiness.domain.port.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListUnitsUseCase {
    private final UnitRepository unitRepository;

    public List<UnitOutputDto> execute() {
        return unitRepository.findAll().stream()
                .map(u -> UnitOutputDto.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .build())
                .collect(Collectors.toList());
    }
}
