// ProductRepository.java
package com.myBusiness.domain.port;

import com.myBusiness.domain.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    Optional<Product> findByName(String name);
    List<Product> findAll();
    void deleteById(Long id);
    Page<Product> findAll(Pageable pageable);
    long count();
}
