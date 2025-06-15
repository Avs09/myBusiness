// src/main/java/com/myBusiness/application/dto/TopProductDto.java
package com.myBusiness.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TopProductDto {
    private Long productId;
    private String productName;
    private long totalExceeded;
}