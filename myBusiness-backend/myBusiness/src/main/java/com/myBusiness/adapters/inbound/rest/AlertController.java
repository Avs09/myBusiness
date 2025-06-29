// src/main/java/com/myBusiness/adapters/inbound/rest/AlertController.java
package com.myBusiness.adapters.inbound.rest;

import com.myBusiness.application.dto.AlertOutputDto;
import com.myBusiness.application.dto.CriticalAlertDto;
import com.myBusiness.application.usecase.DeleteAlertUseCase;
import com.myBusiness.application.usecase.ListAlertsUseCase;
import com.myBusiness.application.usecase.ListUnreadAlertsUseCase;
import com.myBusiness.application.usecase.ListCriticalAlertsUseCase;
import com.myBusiness.application.usecase.MarkAlertReadUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final ListUnreadAlertsUseCase listUnreadUseCase;
    private final ListAlertsUseCase listAllUseCase;
    private final ListCriticalAlertsUseCase listCriticalUseCase;
    private final DeleteAlertUseCase deleteUseCase;
    private final MarkAlertReadUseCase markReadUseCase;

    /**
     * GET /api/alerts/unread → solo alertas no leídas, para polling de notificaciones efímeras.
     */
    @GetMapping("/unread")
    public ResponseEntity<List<AlertOutputDto>> listUnread() {
        List<AlertOutputDto> dto = listUnreadUseCase.execute();
        return ResponseEntity.ok(dto);
    }

    /**
     * GET /api/alerts → devuelve TODO el historial de alertas automáticas.
     */
    @GetMapping
    public ResponseEntity<List<AlertOutputDto>> listAll() {
        List<AlertOutputDto> dto = listAllUseCase.execute();
        return ResponseEntity.ok(dto);
    }

    /**
     * POST /api/alerts/{id}/read → Marca alerta como leída.
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable("id") Long id) {
        markReadUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/alerts/{id} → eliminar alerta permanentemente del historial.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/alerts/critical → devuelve alertas críticas actuales (UNDERSTOCK o OVERSTOCK) no leídas aún.
     * No marca como leídas aquí; se mostrará en tabla de stock crítico y se podrá descartar.
     */
    @GetMapping("/critical")
    public ResponseEntity<List<CriticalAlertDto>> listCritical() {
        List<CriticalAlertDto> dto = listCriticalUseCase.execute();
        return ResponseEntity.ok(dto);
    }
}
