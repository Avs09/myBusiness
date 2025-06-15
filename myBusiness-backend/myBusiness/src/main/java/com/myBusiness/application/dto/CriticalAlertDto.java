// src/main/java/com/myBusiness/application/dto/CriticalAlertDto.java
package com.myBusiness.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
public class CriticalAlertDto {
    private Long id;
    private Long productId;
    private String productName;
    private String alertType;    // "UNDERSTOCK" o "OVERSTOCK"
    private Instant triggeredAt;
    private BigDecimal currentStock;
    private BigDecimal thresholdMin;
    private BigDecimal thresholdMax;
}
