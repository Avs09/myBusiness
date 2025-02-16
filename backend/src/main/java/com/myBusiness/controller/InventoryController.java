package com.myBusiness.controller;

import com.myBusiness.model.Product;
import com.myBusiness.model.User;
import com.myBusiness.repository.UserRepository;
import com.myBusiness.model.InventoryMovement;
import com.myBusiness.service.InventoryService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    
    @Autowired
    private UserRepository userRepository;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // --- Endpoints para productos ---
    
    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product createdProduct = inventoryService.addProduct(product);
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product updatedProduct = inventoryService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        inventoryService.deleteProduct(id);
        return ResponseEntity.ok("Producto eliminado exitosamente.");
    }

    @GetMapping("/products")
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products;

        if (name != null) {
            products = inventoryService.getProductsByName(name, pageable);  // Filtrado por nombre
        } else if (minPrice != null && maxPrice != null) {
            products = inventoryService.getProductsByPriceRange(minPrice, maxPrice, pageable);  // Filtrado por rango de precio
        } else {
            products = inventoryService.getAllProducts(pageable);  // Paginación de todos los productos
        }

        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = inventoryService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // --- Endpoints para movimientos ---
    
    @PostMapping("/movements")
    public ResponseEntity<InventoryMovement> addMovement(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam String type,
            @RequestParam String reason,
            @RequestParam(required = false) String comment,
            @RequestHeader("User-Email") String userEmail) {  // Recibimos el email del usuario desde el header
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));

        InventoryMovement movement = inventoryService.addMovement(productId, quantity, type, reason, comment, user);
        return ResponseEntity.ok(movement);
    }

    @GetMapping("/movements")
    public ResponseEntity<Page<InventoryMovement>> getAllMovements(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryMovement> movements = inventoryService.getAllMovements(pageable);
        return ResponseEntity.ok(movements);
    }
}
