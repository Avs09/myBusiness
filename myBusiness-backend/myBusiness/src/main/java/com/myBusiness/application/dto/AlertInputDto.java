package com.myBusiness.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para crear o actualizar una alerta manualmente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertInputDto {
    @NotNull(message = "productId es obligatorio")
    private Long productId;

    /**
     * Tipo de alerta: UNDERSTOCK o OVERSTOCK
     */
    @NotNull(message = "alertType es obligatorio")
    @Pattern(regexp = "UNDERSTOCK|OVERSTOCK", message = "alertType debe ser UNDERSTOCK o OVERSTOCK")
    private String alertType;

    /**
     * Opcional: si se desea ligar a un movimiento específico
     */
    private Long movementId;
}
