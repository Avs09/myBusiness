// src/main/java/com/myBusiness/domain/port/CustomFieldRepository.java
package com.myBusiness.domain.port;

import com.myBusiness.domain.model.CustomField;

import java.util.List;
import java.util.Optional;

public interface CustomFieldRepository {
    CustomField save(CustomField field);
    Optional<CustomField> findById(Long id);
    List<CustomField> findAllByProductId(Long productId);
    void deleteById(Long id);
}
