// src/main/java/com/myBusiness/application/dto/CategorySummaryDto.java
package com.myBusiness.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategorySummaryDto {
    private Long categoryId;
    private String categoryName;
    private long totalSkus;            // cantidad de productos en esa categoría
    private java.math.BigDecimal totalValue; // suma precio×stock
}
