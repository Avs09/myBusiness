// src/main/java/com/myBusiness/application/dto/MovementTypeCountDto.java
package com.myBusiness.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovementTypeCountDto {
    private String movementType; // "ENTRY", "EXIT", "ADJUSTMENT", etc.
    private long count;
}