// src/main/java/com/myBusiness/application/dto/UnitInputDto.java
package com.myBusiness.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitInputDto {
    @NotBlank(message = "Unit name cannot be empty")
    @Size(max = 50, message = "Unit name cannot exceed 50 characters")
    private String name;
}
