// src/main/java/com/myBusiness/application/usecase/DeleteAlertUseCase.java
package com.myBusiness.application.usecase;

import com.myBusiness.application.exception.AlertNotFoundException;
import com.myBusiness.domain.port.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteAlertUseCase {
    private final AlertRepository alertRepo;

    @Transactional
    public void execute(Long id) {
        if (alertRepo.findById(id).isEmpty()) {
            throw new AlertNotFoundException("Alerta no encontrada id=" + id);
        }
        alertRepo.deleteById(id);
    }
}
