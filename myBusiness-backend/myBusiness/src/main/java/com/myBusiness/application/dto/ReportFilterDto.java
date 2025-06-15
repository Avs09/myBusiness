// ReportFilterDto.java
package com.myBusiness.application.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class ReportFilterDto {
    private Long productId;
    private Long categoryId;
    private Long unitId;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Boolean thresholdBelow; // true=solo bajo umbral
}
