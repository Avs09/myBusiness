package com.myBusiness.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessOutputDto {

    private Long id;
    private String name;
    private String nit;
    private String description;
    private String address;
    private String phone;
    private String email;
    private String website;
    private String logoUrl;
    private String industry;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}