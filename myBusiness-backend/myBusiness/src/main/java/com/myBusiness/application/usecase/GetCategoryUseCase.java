// src/main/java/com/myBusiness/application/usecase/GetCategoryUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.CategoryOutputDto;
import com.myBusiness.application.exception.CategoryNotFoundException;
import com.myBusiness.domain.model.Category;
import com.myBusiness.domain.port.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetCategoryUseCase {
    private final CategoryRepository categoryRepository;

    public CategoryOutputDto execute(Long id) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return CategoryOutputDto.builder()
                .id(cat.getId())
                .name(cat.getName())
                .build();
    }
}
