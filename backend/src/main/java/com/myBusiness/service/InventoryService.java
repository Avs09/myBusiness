package com.myBusiness.service;

import com.myBusiness.model.Product;
import com.myBusiness.model.User;
import com.myBusiness.model.InventoryMovement;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryService {
    Product addProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
    
    // Métodos para paginación de productos y movimientos
    Page<Product> getAllProducts(Pageable pageable);
    Page<Product> getProductsByName(String name, Pageable pageable);  // Filtrado por nombre
    Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);  // Filtrado por precio

    Product getProductById(Long id);

    InventoryMovement addMovement(Long productId, Integer quantity, String type, String reason, String comment, User user);
    Page<InventoryMovement> getAllMovements(Pageable pageable);  // Paginación para movimientos
}
