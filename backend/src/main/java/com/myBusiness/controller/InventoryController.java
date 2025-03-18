package com.myBusiness.controller;

import com.myBusiness.model.Product;
import com.myBusiness.model.User;
import com.myBusiness.model.InventoryMovement;
import com.myBusiness.repository.UserRepository;
import com.myBusiness.service.InventoryService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * InventoryController provides endpoints to manage products and inventory movements.
 * It supports creating, updating, deleting, and retrieving products,
 * as well as recording and listing inventory movements.
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    // Service handling inventory operations
    private final InventoryService inventoryService;
    
    // Repository for retrieving user information by email
    private final UserRepository userRepository;

    /**
     * Constructor for dependency injection.
     */
    public InventoryController(InventoryService inventoryService, UserRepository userRepository) {
        this.inventoryService = inventoryService;
        this.userRepository = userRepository;
    }

    // --- Endpoints for product management ---

    /**
     * Endpoint to add a new product.
     *
     * @param product The product details.
     * @return The created product with status 201 (Created).
     */
    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@Valid @RequestBody Product product) {
        Product createdProduct = inventoryService.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Endpoint to update an existing product.
     *
     * @param id The ID of the product to update.
     * @param product The updated product details.
     * @return The updated product.
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        Product updatedProduct = inventoryService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Endpoint to delete a product by its ID.
     *
     * @param id The product's ID.
     * @return A confirmation message.
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        inventoryService.deleteProduct(id);
        return ResponseEntity.ok("Producto eliminado exitosamente.");
    }

    /**
     * Endpoint to retrieve products with optional filtering by name or price range,
     * as well as support for pagination.
     *
     * @param page Page number for pagination.
     * @param size Number of records per page.
     * @param name Optional parameter to filter products by name.
     * @param minPrice Optional minimum price filter.
     * @param maxPrice Optional maximum price filter.
     * @return A paginated list of products.
     */
    @GetMapping("/products")
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products;

        // Apply filters based on provided query parameters
        if (name != null) {
            products = inventoryService.getProductsByName(name, pageable);
        } else if (minPrice != null && maxPrice != null) {
            products = inventoryService.getProductsByPriceRange(minPrice, maxPrice, pageable);
        } else {
            products = inventoryService.getAllProducts(pageable);
        }

        return ResponseEntity.ok(products);
    }

    /**
     * Endpoint to retrieve a specific product by its ID.
     *
     * @param id The product's ID.
     * @return The product details.
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = inventoryService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // --- Endpoints for inventory movements ---

    /**
     * Endpoint to add an inventory movement.
     * Validates the user by email from the request header.
     *
     * @param productId The ID of the product.
     * @param quantity The quantity moved.
     * @param type The type of movement (e.g., IN, OUT).
     * @param reason The reason for the movement.
     * @param comment Optional comment for additional details.
     * @param userEmail The email of the user performing the operation.
     * @return The created inventory movement record with status 201 (Created).
     */
    @PostMapping("/movements")
    public ResponseEntity<InventoryMovement> addMovement(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam String type,
            @RequestParam String reason,
            @RequestParam(required = false) String comment,
            @RequestHeader("User-Email") String userEmail) {
        // Retrieve user information using the provided email
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));

        InventoryMovement movement = inventoryService.addMovement(productId, quantity, type, reason, comment, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(movement);
    }

    /**
     * Endpoint to retrieve paginated inventory movements.
     *
     * @param page Page number for pagination.
     * @param size Number of records per page.
     * @return A paginated list of inventory movements.
     */
    @GetMapping("/movements")
    public ResponseEntity<Page<InventoryMovement>> getAllMovements(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryMovement> movements = inventoryService.getAllMovements(pageable);
        return ResponseEntity.ok(movements);
    }
}
