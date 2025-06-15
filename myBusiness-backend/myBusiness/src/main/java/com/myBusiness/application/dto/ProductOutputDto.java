package com.myBusiness.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
public class ProductOutputDto {

    private Long id;
    private String name;

    // Umbrales configurados
    private Integer thresholdMin;
    private Integer thresholdMax;

    private BigDecimal price;

    // Además de los nombres, expongo los IDs
    private Long categoryId;
    private String categoryName;

    private Long unitId;
    private String unitName;

    // Stock “real” calculado a partir de movimientos
    private BigDecimal currentStock;

    // Auditoría
    private Instant createdDate;
    private String createdBy;
    private Instant modifiedDate;
    private String modifiedBy;
}
