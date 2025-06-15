package com.myBusiness.domain.port;

import com.myBusiness.domain.model.Alert;

import java.util.List;
import java.util.Optional;

public interface AlertRepository {
    Alert save(Alert alert);
    Optional<Alert> findById(Long id);
    List<Alert> findAllUnread();
    List<Alert> findAllByProductId(Long productId);
    List<Alert> findAll();  // agregado para historial
    void deleteById(Long id);
}
