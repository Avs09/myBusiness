// src/main/java/com/myBusiness/application/dto/FieldValueOutputDto.java
package com.myBusiness.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Builder
public class FieldValueOutputDto {
    private Long id;
    private Long productId;
    private Long fieldId;
    private String valueText;
    private Double valueNumber;
    private LocalDate valueDate;
    private Instant createdAt;
    private String createdBy;
    private Instant createdDate;
    private String modifiedBy;
    private Instant modifiedDate;
}
