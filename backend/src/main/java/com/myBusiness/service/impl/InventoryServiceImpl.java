package com.myBusiness.service.impl;

import com.myBusiness.model.Product;
import com.myBusiness.model.User;
import com.myBusiness.model.InventoryMovement;
import com.myBusiness.repository.ProductRepository;
import com.myBusiness.repository.InventoryMovementRepository;
import com.myBusiness.service.InventoryService;
import com.myBusiness.util.DataSanitizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * InventoryServiceImpl provides the concrete implementation of the InventoryService interface.
 * It handles product management, inventory movement operations, and ensures business validations.
 * <p>
 * Methods that modify data are annotated with @Transactional to ensure atomicity.
 * </p>
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final ProductRepository productRepository;
    private final InventoryMovementRepository movementRepository;

    public InventoryServiceImpl(ProductRepository productRepository, InventoryMovementRepository movementRepository) {
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
    }

    /**
     * Adds a new product after sanitizing and validating its information.
     * This operation is transactional to ensure the product is correctly persisted.
     *
     * @param product the product to add.
     * @return the saved product with its generated identifier.
     * @throws IllegalArgumentException if any required product field is invalid.
     */
    @Override
    @Transactional
    public Product addProduct(Product product) {
        // Sanitize text fields before validation
        product.setName(DataSanitizer.sanitize(product.getName()));
        product.setDescription(DataSanitizer.sanitize(product.getDescription()));

        validateProduct(product);
        Product savedProduct = productRepository.save(product);
        logger.info("Product created: {} (ID: {})", product.getName(), savedProduct.getId());
        return savedProduct;
    }

    /**
     * Updates an existing product's details after sanitizing and validating the input.
     * The operation is transactional to ensure data consistency.
     *
     * @param id      the ID of the product to update.
     * @param product the updated product information.
     * @return the updated product.
     * @throws EntityNotFoundException if the product does not exist.
     * @throws IllegalArgumentException if the product data is invalid.
     */
    @Override
    @Transactional
    public Product updateProduct(Long id, Product product) {
        // Sanitize the incoming fields before processing
        product.setName(DataSanitizer.sanitize(product.getName()));
        product.setDescription(DataSanitizer.sanitize(product.getDescription()));

        validateProduct(product);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Attempted update for non-existent product with ID: {}", id);
                    return new EntityNotFoundException("Product not found.");
                });

        // Update product attributes
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setPrice(product.getPrice());

        Product updatedProduct = productRepository.save(existingProduct);
        logger.info("Product updated: {} (ID: {})", updatedProduct.getName(), updatedProduct.getId());
        return updatedProduct;
    }

    /**
     * Retrieves products that contain the specified name (case insensitive) with pagination.
     *
     * @param name     the product name filter.
     * @param pageable pagination information.
     * @return a page of products matching the name filter.
     */
    @Override
    public Page<Product> getProductsByName(String name, Pageable pageable) {
        Page<Product> products = productRepository.findByNameContainingIgnoreCase(name, pageable);
        logger.info("Retrieved {} products with name filter: {}", products.getTotalElements(), name);
        return products;
    }

    /**
     * Deletes a product after ensuring it exists.
     * Transactional to guarantee that the deletion occurs atomically.
     *
     * @param id the ID of the product to delete.
     * @throws EntityNotFoundException if the product does not exist.
     */
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Attempted deletion for non-existent product with ID: {}", id);
                    return new EntityNotFoundException("Product not found.");
                });

        productRepository.delete(product);
        logger.info("Product deleted: {} (ID: {})", product.getName(), id);
    }

    /**
     * Retrieves all products with pagination.
     *
     * @param pageable pagination information.
     * @return a page of products.
     */
    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        logger.info("Retrieved {} products from inventory.", products.getTotalElements());
        return products;
    }

    /**
     * Retrieves a specific product by its ID.
     *
     * @param id the product's ID.
     * @return the product details.
     * @throws EntityNotFoundException if the product is not found.
     */
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Product not found with ID: {}", id);
                    return new EntityNotFoundException("Product not found.");
                });
    }

    /**
     * Adds an inventory movement record (entry or exit) for a product.
     * Adjusts the product quantity accordingly.
     * The method is transactional to ensure both product update and movement recording occur atomically.
     *
     * @param productId the product's ID.
     * @param quantity  the quantity involved in the movement (must be positive).
     * @param type      the type of movement ("ENTRADA" for addition, "SALIDA" for removal).
     * @param reason    the reason for the movement (optional).
     * @param comment   additional comments regarding the movement (optional).
     * @param user      the user performing the operation.
     * @return the recorded inventory movement.
     * @throws IllegalArgumentException if quantity is non-positive or if there's insufficient stock.
     * @throws EntityNotFoundException if the product is not found.
     */
    @Override
    @Transactional
    public InventoryMovement addMovement(Long productId, Integer quantity, String type, String reason, String comment, User user) {
        if (quantity <= 0) {
            logger.warn("Invalid quantity for movement: {}", quantity);
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.warn("Product not found for movement with ID: {}", productId);
                    return new EntityNotFoundException("Product not found.");
                });

        // Check stock for exit movements
        if ("SALIDA".equalsIgnoreCase(type) && product.getQuantity() < quantity) {
            logger.warn("Insufficient stock for product: {} (ID: {})", product.getName(), productId);
            throw new IllegalArgumentException("Insufficient stock for removal.");
        }

        // Create a new inventory movement record
        InventoryMovement movement = new InventoryMovement();
        movement.setProduct(product);
        movement.setQuantity(quantity);
        movement.setType(InventoryMovement.MovementType.valueOf(type.toUpperCase()));
        movement.setDate(Instant.now());
        movement.setReason(reason);
        movement.setComment(comment);
        movement.setUser(user);

        // Adjust product quantity based on movement type
        if ("ENTRADA".equalsIgnoreCase(type)) {
            product.setQuantity(product.getQuantity() + quantity);
        } else if ("SALIDA".equalsIgnoreCase(type)) {
            product.setQuantity(product.getQuantity() - quantity);
        }

        // Save changes to product and record the movement atomically
        productRepository.save(product);
        InventoryMovement savedMovement = movementRepository.save(movement);

        logger.info("Movement recorded: {} (Product: {}, Quantity: {}, Type: {})",
                savedMovement.getId(), product.getName(), quantity, type);
        return savedMovement;
    }

    /**
     * Retrieves all inventory movements with pagination.
     *
     * @param pageable pagination information.
     * @return a page of inventory movement records.
     */
    @Override
    public Page<InventoryMovement> getAllMovements(Pageable pageable) {
        return movementRepository.findAll(pageable);
    }

    /**
     * Retrieves products within a specified price range.
     *
     * @param minPrice the minimum price (inclusive).
     * @param maxPrice the maximum price (inclusive).
     * @param pageable pagination information.
     * @return a page of products whose prices fall within the specified range.
     */
    @Override
    public Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Page<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        logger.info("Retrieved {} products within price range: {} - {}", products.getTotalElements(), minPrice, maxPrice);
        return products;
    }

    /**
     * Validates product data to ensure mandatory fields are present and valid.
     *
     * @param product the product to validate.
     * @throws IllegalArgumentException if any validation constraint is violated.
     */
    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (product.getDescription() == null || product.getDescription().isBlank()) {
            throw new IllegalArgumentException("Product description cannot be empty.");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero.");
        }
        if (product.getQuantity() == null || product.getQuantity() < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative.");
        }
    }
}
