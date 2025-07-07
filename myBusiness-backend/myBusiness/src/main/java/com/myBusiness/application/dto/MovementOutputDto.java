// src/main/java/com/myBusiness/application/dto/MovementOutputDto.java
package com.myBusiness.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MovementOutputDto {
    private Long id;
    private Long productId;
    private String productName;      
    private String movementType;
    private BigDecimal quantity;
    private String reason;
    private Instant movementDate;
    private String createdBy;
    private Instant createdDate;
    private String modifiedBy;
    private Instant modifiedDate;
}
