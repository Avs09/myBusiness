// src/main/java/com/myBusiness/application/dto/CustomFieldOutputDto.java
package com.myBusiness.application.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomFieldOutputDto {
    private Long id;
    private Long productId;
    private String name;
    private String dataType;
    private String createdBy;
    private Instant createdDate;
    private String modifiedBy;
    private Instant modifiedDate;
}
