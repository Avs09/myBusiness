package com.myBusiness.repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.myBusiness.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
    
    // Añadimos el método para obtener productos con paginación
    Page<Product> findAll(Pageable pageable);
    
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // Método para buscar productos por nombre
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    List<Product> findByQuantityLessThan(int quantity);
}
