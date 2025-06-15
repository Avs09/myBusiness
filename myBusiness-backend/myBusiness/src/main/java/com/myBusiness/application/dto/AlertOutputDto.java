// src/main/java/com/myBusiness/application/dto/AlertOutputDto.java
package com.myBusiness.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class AlertOutputDto {
    private Long id;
    private Long productId;
    private String productName;
    private Long movementId;
    private String alertType;       // "UNDERSTOCK" o "OVERSTOCK"
    private Instant triggeredAt;
    private Instant createdDate;
    private Integer thresholdMin;
    private Integer thresholdMax;
    // omit campos isRead, createdBy, modifiedBy, modifiedDate según petición
}
