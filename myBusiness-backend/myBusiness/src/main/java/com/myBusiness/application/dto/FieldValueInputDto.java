// src/main/java/com/myBusiness/application/dto/FieldValueInputDto.java
package com.myBusiness.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class FieldValueInputDto {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Field ID is required")
    private Long fieldId;

    private String valueText;
    private Double valueNumber;
    private LocalDate valueDate;
}
