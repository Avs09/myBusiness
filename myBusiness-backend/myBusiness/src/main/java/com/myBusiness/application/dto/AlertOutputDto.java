package com.myBusiness.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
public class AlertOutputDto {
    private Long id;
    private Long productId;
    private String productName;
    private Long movementId;
    private String alertType;
    private Instant triggeredAt;
    private Instant createdDate;
    private Integer thresholdMin;
    private Integer thresholdMax;
    private BigDecimal currentStock;    // ← Nuevo campo para stock actual
}