// src/main/java/com/myBusiness/application/dto/ProductInputDto.java
package com.myBusiness.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

import com.myBusiness.application.validation.ConsistentThreshold;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ConsistentThreshold
public class ProductInputDto {

 @NotBlank(message = "name must not be blank")
 private String name;

 @Min(value = 0, message = "thresholdMin must be >= 0")
 private int thresholdMin;

 @Min(value = 0, message = "thresholdMax must be >= 0")
 private int thresholdMax;

 @NotNull(message = "price is required")
 @DecimalMin(value = "0.0", inclusive = true, message = "price must be >= 0.0")
 private BigDecimal price;

 @NotNull(message = "categoryId is required")
 private Long categoryId;

 @NotNull(message = "unitId is required")
 private Long unitId;
}
