// src/main/java/com/myBusiness/domain/port/CategoryRepository.java
package com.myBusiness.domain.port;

import com.myBusiness.domain.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(Long id);
    List<Category> findAll();
    void deleteById(Long id);
}
