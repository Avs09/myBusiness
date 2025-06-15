// src/main/java/com/myBusiness/application/usecase/DeleteUnitUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.exception.UnitNotFoundException;
import com.myBusiness.domain.port.UnitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteUnitUseCase {
    private final UnitRepository unitRepository;

    @Transactional
    public void execute(Long id) {
        if (unitRepository.findById(id).isEmpty()) {
            throw new UnitNotFoundException(id);
        }
        unitRepository.deleteById(id);
    }
}
