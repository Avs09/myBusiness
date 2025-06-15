package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.NotificationDto;
import com.myBusiness.domain.model.Notification;
import com.myBusiness.domain.port.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListAllNotificationsUseCase {

    private final NotificationRepository notificationRepo;

    public List<NotificationDto> execute() {
        List<Notification> all = notificationRepo.findAll(); 
        return all.stream()
            .map(n -> NotificationDto.builder()
                .id(n.getId())
                .message(n.getMessage())
                .createdDate(n.getCreatedDate())
                .isRead(n.isRead())
                .build())
            .collect(Collectors.toList());
    }
}
