// src/main/java/com/myBusiness/application/dto/StockByDateDto.java
package com.myBusiness.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class StockByDateDto {
    private String date;       // ej. “2025-06-01”
    private BigDecimal totalStock;
}
