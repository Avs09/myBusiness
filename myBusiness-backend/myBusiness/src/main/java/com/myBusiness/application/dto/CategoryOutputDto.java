package com.myBusiness.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryOutputDto {
    private Long id;
    private String name;
}
