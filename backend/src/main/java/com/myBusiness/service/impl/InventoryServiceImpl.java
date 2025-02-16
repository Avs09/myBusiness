package com.myBusiness.service.impl;

import com.myBusiness.model.Product;
import com.myBusiness.model.User;
import com.myBusiness.model.InventoryMovement;
import com.myBusiness.repository.ProductRepository;
import com.myBusiness.repository.InventoryMovementRepository;
import com.myBusiness.service.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class InventoryServiceImpl implements InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final ProductRepository productRepository;
    private final InventoryMovementRepository movementRepository;

    public InventoryServiceImpl(ProductRepository productRepository, InventoryMovementRepository movementRepository) {
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
    }

    @Override
    public Product addProduct(Product product) {
        validateProduct(product);
        Product savedProduct = productRepository.save(product);
        logger.info("Producto creado: {} (ID: {})", product.getName(), savedProduct.getId());
        return savedProduct;
    }
    
    public Page<Product> getProductsByName(String name, Pageable pageable) {
        Page<Product> products = productRepository.findByNameContainingIgnoreCase(name, pageable);
        logger.info("Se recuperaron {} productos con el nombre: {}", products.getTotalElements(), name);
        return products;
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        validateProduct(product);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Intento de actualización de producto no encontrado: ID {}", id);
                    return new EntityNotFoundException("Producto no encontrado.");
                });

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setPrice(product.getPrice());

        Product updatedProduct = productRepository.save(existingProduct);
        logger.info("Producto actualizado: {} (ID: {})", updatedProduct.getName(), updatedProduct.getId());
        return updatedProduct;
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Intento de eliminación de producto no encontrado: ID {}", id);
                    return new EntityNotFoundException("Producto no encontrado.");
                });

        productRepository.delete(product);
        logger.info("Producto eliminado: {} (ID: {})", product.getName(), id);
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        logger.info("Se recuperaron {} productos del inventario.", products.getTotalElements());
        return products;
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Intento de recuperación de producto no encontrado: ID {}", id);
                    return new EntityNotFoundException("Producto no encontrado.");
                });
    }

    @Override
    public InventoryMovement addMovement(Long productId, Integer quantity, String type, String reason, String comment, User user) {
        if (quantity <= 0) {
            logger.warn("Cantidad inválida para el movimiento: {}", quantity);
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.warn("Producto no encontrado para movimiento: ID {}", productId);
                    return new EntityNotFoundException("Producto no encontrado.");
                });

        if ("SALIDA".equalsIgnoreCase(type) && product.getQuantity() < quantity) {
            logger.warn("Stock insuficiente para producto: {} (ID: {})", product.getName(), productId);
            throw new IllegalArgumentException("Stock insuficiente para realizar la salida.");
        }

        InventoryMovement movement = new InventoryMovement();
        movement.setProduct(product);
        movement.setQuantity(quantity);
        movement.setType(type);
        movement.setDate(Instant.now());
        movement.setReason(reason);  // Establecer razón
        movement.setComment(comment);  // Establecer comentario
        movement.setUser(user);  // Establecer usuario que realiza el movimiento

        if ("ENTRADA".equalsIgnoreCase(type)) {
            product.setQuantity(product.getQuantity() + quantity);
        } else if ("SALIDA".equalsIgnoreCase(type)) {
            product.setQuantity(product.getQuantity() - quantity);
        }

        productRepository.save(product);
        InventoryMovement savedMovement = movementRepository.save(movement);

        logger.info("Movimiento registrado: {} (Producto: {}, Cantidad: {}, Tipo: {})",
                savedMovement.getId(), product.getName(), quantity, type);
        return savedMovement;
    }

    @Override
    public Page<InventoryMovement> getAllMovements(Pageable pageable) {
        return movementRepository.findAll(pageable);
    }

    @Override
    public Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Page<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        logger.info("Se recuperaron {} productos dentro del rango de precios: {} - {}", products.getTotalElements(), minPrice, maxPrice);
        return products;
    }

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
        }
        if (product.getDescription() == null || product.getDescription().isBlank()) {
            throw new IllegalArgumentException("La descripción del producto no puede estar vacía.");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio del producto debe ser mayor que cero.");
        }
        if (product.getQuantity() == null || product.getQuantity() < 0) {
            throw new IllegalArgumentException("La cantidad del producto no puede ser negativa.");
        }
    }
}
