// src/main/java/com/myBusiness/application/dto/CategoryInputDto.java
package com.myBusiness.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryInputDto {
    @NotBlank(message = "Category name cannot be empty")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    private String name;
}
