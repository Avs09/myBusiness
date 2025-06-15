// src/main/java/com/myBusiness/application/usecase/DeleteCategoryUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.exception.CategoryNotFoundException;
import com.myBusiness.domain.port.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteCategoryUseCase {
    private final CategoryRepository categoryRepository;

    @Transactional
    public void execute(Long id) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new CategoryNotFoundException(id);
        }
        categoryRepository.deleteById(id);
    }
}
