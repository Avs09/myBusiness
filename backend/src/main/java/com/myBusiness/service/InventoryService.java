package com.myBusiness.service;

import com.myBusiness.model.Product;
import com.myBusiness.model.User;
import com.myBusiness.model.InventoryMovement;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * InventoryService defines the contract for all inventory-related operations.
 * <p>
 * This service manages products and records inventory movements (e.g., stock additions and removals).
 * Implementations should ensure proper validation of input data and handle potential errors,
 * such as non-existent products or invalid movement parameters.
 * </p>
 */
public interface InventoryService {
    
    /**
     * Adds a new product to the inventory.
     *
     * @param product the product entity to add. Must include required fields.
     * @return the saved product entity with its generated identifier.
     * @throws IllegalArgumentException if the product data is invalid.
     */
    Product addProduct(Product product);
    
    /**
     * Updates an existing product identified by its ID.
     *
     * @param id the unique identifier of the product to update.
     * @param product the product entity containing updated information.
     * @return the updated product entity.
     * @throws IllegalArgumentException if the product data is invalid or the product does not exist.
     */
    Product updateProduct(Long id, Product product);
    
    /**
     * Deletes a product from the inventory using its ID.
     *
     * @param id the unique identifier of the product to delete.
     * @throws IllegalArgumentException if the product does not exist.
     */
    void deleteProduct(Long id);
    
    /**
     * Retrieves a paginated list of all products.
     *
     * @param pageable pagination information including page number and size.
     * @return a page of products.
     */
    Page<Product> getAllProducts(Pageable pageable);
    
    /**
     * Searches for products by name with pagination.
     *
     * @param name the product name filter (partial or full name).
     * @param pageable pagination information.
     * @return a page of products matching the specified name criteria.
     */
    Page<Product> getProductsByName(String name, Pageable pageable);
    
    /**
     * Retrieves products within a specified price range.
     *
     * @param minPrice the minimum price (inclusive).
     * @param maxPrice the maximum price (inclusive).
     * @param pageable pagination information.
     * @return a page of products whose prices fall within the specified range.
     */
    Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    /**
     * Retrieves a single product by its ID.
     *
     * @param id the unique identifier of the product.
     * @return the product details.
     * @throws IllegalArgumentException if the product does not exist.
     */
    Product getProductById(Long id);
    
    /**
     * Records an inventory movement (e.g., stock addition or removal).
     *
     * @param productId the unique identifier of the product.
     * @param quantity the quantity involved in the movement (should be positive).
     * @param type the type of movement (e.g., "ENTRADA" for addition, "SALIDA" for removal).
     *             It is recommended to use predefined types or enums in the implementation.
     * @param reason the reason for the movement (optional).
     * @param comment additional comments regarding the movement (optional).
     * @param user the user performing the operation.
     * @return the recorded inventory movement entity.
     * @throws IllegalArgumentException if any parameter is invalid.
     */
    InventoryMovement addMovement(Long productId, Integer quantity, String type, String reason, String comment, User user);
    
    /**
     * Retrieves a paginated list of inventory movements.
     *
     * @param pageable pagination information.
     * @return a page of inventory movement records.
     */
    Page<InventoryMovement> getAllMovements(Pageable pageable);
}
