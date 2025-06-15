// src/main/java/com/myBusiness/application/dto/ReportSummaryDto.java
package com.myBusiness.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummaryDto {
    private long totalSkus;
    private String totalValue;     // BigDecimal convertido a String, p.ej. "12345.67"
    private int daysOfStock;       // rotación, valor entero o según tu cálculo
}
