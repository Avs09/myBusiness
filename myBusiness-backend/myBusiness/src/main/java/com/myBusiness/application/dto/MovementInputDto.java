// src/main/java/com/myBusiness/application/dto/MovementInputDto.java
package com.myBusiness.application.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO para crear o editar un movimiento.
 */
@Getter
@Setter
public class MovementInputDto {
    @NotNull
    private Long productId;

    @NotBlank
    private String movementType; // "ENTRY","EXIT","ADJUSTMENT"

    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    private BigDecimal quantity;

    private String reason;
}
