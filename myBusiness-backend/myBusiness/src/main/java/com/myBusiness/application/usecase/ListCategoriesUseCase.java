// src/main/java/com/myBusiness/application/usecase/ListCategoriesUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.CategoryOutputDto;
import com.myBusiness.domain.port.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListCategoriesUseCase {
    private final CategoryRepository categoryRepository;

    public List<CategoryOutputDto> execute() {
        return categoryRepository.findAll().stream()
                .map(cat -> CategoryOutputDto.builder()
                        .id(cat.getId())
                        .name(cat.getName())
                        .build())
                .collect(Collectors.toList());
    }
}
