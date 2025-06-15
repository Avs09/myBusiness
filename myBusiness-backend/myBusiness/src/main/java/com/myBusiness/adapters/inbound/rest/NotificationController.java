// src/main/java/com/myBusiness/adapters/inbound/rest/NotificationController.java
package com.myBusiness.adapters.inbound.rest;

import com.myBusiness.application.dto.NotificationDto;
import com.myBusiness.application.usecase.ListAllNotificationsUseCase;
import com.myBusiness.application.usecase.ListUnreadNotificationsUseCase;
import com.myBusiness.application.usecase.MarkNotificationAsReadUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final ListUnreadNotificationsUseCase listUnreadUseCase;
    private final ListAllNotificationsUseCase listAllUseCase;
    private final MarkNotificationAsReadUseCase markAsReadUseCase;

    /** GET /api/notifications/unread → solo las no leídas */
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDto>> getUnread() {
        List<NotificationDto> dto = listUnreadUseCase.execute();
        return ResponseEntity.ok(dto);
    }

    /** GET /api/notifications/all → todas (leídas y no leídas) */
    @GetMapping("/all")
    public ResponseEntity<List<NotificationDto>> getAll() {
        List<NotificationDto> dto = listAllUseCase.execute();
        return ResponseEntity.ok(dto);
    }

    /** POST /api/notifications/{id}/read → marca como leída */
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        markAsReadUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
