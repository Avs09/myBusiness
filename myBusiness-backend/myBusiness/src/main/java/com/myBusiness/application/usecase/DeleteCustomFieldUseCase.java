// src/main/java/com/myBusiness/application/usecase/DeleteCustomFieldUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.exception.CustomFieldNotFoundException;
import com.myBusiness.domain.port.CustomFieldRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteCustomFieldUseCase {
    private final CustomFieldRepository fieldRepo;

    @Transactional
    public void execute(Long fieldId) {
        if (fieldRepo.findById(fieldId).isEmpty()) {
            throw new CustomFieldNotFoundException(fieldId);
        }
        fieldRepo.deleteById(fieldId);
    }
}
