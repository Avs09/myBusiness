package com.myBusiness.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class NotificationDto {
    private Long id;
    private String message;
    private Instant createdDate;
    private boolean isRead;
}
