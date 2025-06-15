// src/main/java/com/myBusiness/application/usecase/CreateCategoryUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.CategoryInputDto;
import com.myBusiness.application.dto.CategoryOutputDto;
import com.myBusiness.application.exception.InvalidCategoryException;
import com.myBusiness.domain.model.Category;
import com.myBusiness.domain.port.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCategoryUseCase {
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryOutputDto execute(CategoryInputDto input) {
        String name = input.getName().trim();
        if (name.isEmpty()) {
            throw new InvalidCategoryException("Category name cannot be empty");
        }
        Category entity = Category.builder()
                .name(name)
                .build();
        Category saved = categoryRepository.save(entity);
        return CategoryOutputDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .build();
    }
}
