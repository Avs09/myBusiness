// src/main/java/com/myBusiness/application/dto/DailyMovementCountDto.java
package com.myBusiness.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyMovementCountDto {
    // Fecha en formato "YYYY-MM-DD"
    private String date;
    private long count;
}