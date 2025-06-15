// src/main/java/com/myBusiness/domain/port/FieldValueRepository.java
package com.myBusiness.domain.port;

import com.myBusiness.domain.model.FieldValue;

import java.util.List;
import java.util.Optional;

public interface FieldValueRepository {
    FieldValue save(FieldValue value);
    Optional<FieldValue> findById(Long id);
    List<FieldValue> findAllByProductIdAndFieldId(Long productId, Long fieldId);
    void deleteById(Long id);
}
