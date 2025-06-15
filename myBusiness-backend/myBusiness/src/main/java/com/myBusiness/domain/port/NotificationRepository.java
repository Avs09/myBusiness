package com.myBusiness.domain.port;

import com.myBusiness.domain.model.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);
    Optional<Notification> findById(Long id);
    List<Notification> findAllUnread();
    List<Notification> findAll();      
    void markAsRead(Long id);
}
