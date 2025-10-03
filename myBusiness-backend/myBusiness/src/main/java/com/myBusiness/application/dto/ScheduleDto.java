// src/main/java/com/myBusiness/application/dto/ScheduleDto.java
package com.myBusiness.application.dto;

import lombok.Data;

@Data
public class ScheduleDto {
    private String email;
    private String frequency; // "DAILY" or "WEEKLY"

    // Report filters (optional)
    private Long productId;
    private Long categoryId;
    private Long unitId;
    private String dateFrom;
    private String dateTo;
    private String movementType;
    private Boolean thresholdBelow;
}