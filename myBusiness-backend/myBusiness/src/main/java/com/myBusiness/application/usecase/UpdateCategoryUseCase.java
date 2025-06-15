// src/main/java/com/myBusiness/application/usecase/UpdateCategoryUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.CategoryInputDto;
import com.myBusiness.application.dto.CategoryOutputDto;
import com.myBusiness.application.exception.CategoryNotFoundException;
import com.myBusiness.application.exception.InvalidCategoryException;
import com.myBusiness.domain.model.Category;
import com.myBusiness.domain.port.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateCategoryUseCase {
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryOutputDto execute(Long id, CategoryInputDto input) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        String name = input.getName().trim();
        if (name.isEmpty()) {
            throw new InvalidCategoryException("Category name cannot be empty");
        }
        cat.setName(name);
        Category updated = categoryRepository.save(cat);
        return CategoryOutputDto.builder()
                .id(updated.getId())
                .name(updated.getName())
                .build();
    }
}
