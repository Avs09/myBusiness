// src/main/java/com/myBusiness/application/dto/UserProfileDto.java
package com.myBusiness.application.dto;

import lombok.Data;

@Data
public class UserProfileDto {
    private String name;
    private String phone;
    private String location;
    private String bio;
}