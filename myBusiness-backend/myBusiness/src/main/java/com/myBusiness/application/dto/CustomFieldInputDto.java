// src/main/java/com/myBusiness/application/dto/CustomFieldInputDto.java
package com.myBusiness.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CustomFieldInputDto {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotBlank(message = "Field name cannot be empty")
    private String name;

    @NotBlank(message = "Data type is required (text, number or date)")
    private String dataType;
}
