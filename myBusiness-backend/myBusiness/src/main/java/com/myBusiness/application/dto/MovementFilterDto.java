// src/main/java/com/myBusiness/application/dto/MovementFilterDto.java
package com.myBusiness.application.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

/**
 * Filtros para listado paginado de movimientos.
 * Se enlaza automáticamente desde query params:
 *  - page, size
 *  - productId
 *  - dateFrom, dateTo  (format “YYYY-MM-DD”)
 *  - movementType (“ENTRY”, “EXIT”, “ADJUSTMENT”)
 *  - search: texto para buscar en motivo o nombre de producto
 *  - sort: e.g. "movementDate,desc" o "id,asc"
 */
@Data
public class MovementFilterDto {
    @Min(0)
    private Integer page = 0;

    @Min(1)
    private Integer size = 10;

    private Long productId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    /**
     * “ENTRY”, “EXIT” o “ADJUSTMENT” (no sensible a mayúsculas)
     */
    private String movementType;

    private String search;

    /**
     * Ordenamiento: "field,direction", e.g. "movementDate,desc". Opcional.
     */
    private String sort;
}
