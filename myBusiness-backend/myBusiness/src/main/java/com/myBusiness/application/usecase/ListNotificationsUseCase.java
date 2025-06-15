package com.myBusiness.application.usecase;

import com.myBusiness.application.dto.NotificationDto;
import com.myBusiness.domain.model.Notification;
import com.myBusiness.domain.port.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Este UseCase se encarga de listar todas las notificaciones NO leídas.
 * Spring lo registrará como bean (gracias a @Service).
 */
@Service
@RequiredArgsConstructor
public class ListNotificationsUseCase {

    private final NotificationRepository notificationRepo;

    /**
     * Devuelve un List<NotificationDto> con aquellas notificaciones cuyo campo isRead == false.
     */
    public List<NotificationDto> execute() {
        List<Notification> unreadEntities = notificationRepo.findAllUnread();
        return unreadEntities.stream()
                .map(n -> NotificationDto.builder()
                        .id(n.getId())
                        .message(n.getMessage())
                        .createdDate(n.getCreatedDate())
                        .isRead(n.isRead())
                        .build())
                .collect(Collectors.toList());
    }
}
