// src/main/java/com/myBusiness/application/dto/DashboardMetricsDto.java
package com.myBusiness.application.dto;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class DashboardMetricsDto {
    private long totalProducts;           
    private String totalInventoryValue;   
    private long totalOpenAlerts;         
    private long movementsLast7Days;      
}
