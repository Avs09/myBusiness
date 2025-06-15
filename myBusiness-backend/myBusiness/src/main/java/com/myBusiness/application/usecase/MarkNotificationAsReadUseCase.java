package com.myBusiness.application.usecase;

import com.myBusiness.domain.port.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase que marca una notificación como leída (isRead = true).
 */
@Service
@RequiredArgsConstructor
public class MarkNotificationAsReadUseCase {

    private final NotificationRepository notificationRepo;

    @Transactional
    public void execute(Long id) {
        notificationRepo.markAsRead(id);
    }
}
