// src/main/java/com/myBusiness/application/usecase/MarkAlertReadUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.exception.AlertNotFoundException;
import com.myBusiness.domain.model.Alert;
import com.myBusiness.domain.port.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarkAlertReadUseCase {
    private final AlertRepository alertRepo;

    @Transactional
    public void execute(Long id) {
        Alert a = alertRepo.findById(id)
            .orElseThrow(() -> new AlertNotFoundException("Alerta no encontrada id=" + id));
        if (!a.isRead()) {
            a.setRead(true);
            alertRepo.save(a);
        }
    }
}
