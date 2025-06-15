// src/main/java/com/myBusiness/application/usecase/DeleteFieldValueUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.exception.FieldValueNotFoundException;
import com.myBusiness.domain.port.FieldValueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteFieldValueUseCase {
    private final FieldValueRepository valueRepo;

    @Transactional
    public void execute(Long valueId) {
        if (valueRepo.findById(valueId).isEmpty()) {
            throw new FieldValueNotFoundException(valueId);
        }
        valueRepo.deleteById(valueId);
    }
}
