// src/main/java/com/myBusiness/application/dto/UnitOutputDto.java
package com.myBusiness.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UnitOutputDto {
    private Long id;
    private String name;
}
