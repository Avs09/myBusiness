// InventoryReportRowDto.java
package com.myBusiness.application.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
public class InventoryReportRowDto {
    private Long productId;
    private String productName;
    private String categoryName;
    private String unitName;
    private BigDecimal currentStock;
    private Instant lastMovementDate;
}
