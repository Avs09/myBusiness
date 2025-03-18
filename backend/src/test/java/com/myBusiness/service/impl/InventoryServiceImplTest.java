package com.myBusiness.service.impl;

import com.myBusiness.model.InventoryMovement;
import com.myBusiness.model.Product;
import com.myBusiness.model.User;
import com.myBusiness.repository.InventoryMovementRepository;
import com.myBusiness.repository.ProductRepository;
import com.myBusiness.util.DataSanitizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * InventoryServiceImplTest contains unit tests for the InventoryServiceImpl class.
 * It uses Mockito to simulate repository behavior and verifies that the service
 * methods perform data sanitization, validation, and business logic correctly.
 */
@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryMovementRepository movementRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Product product;

    @BeforeEach
    void setUp() {
        // Initialize a sample product with unsanitized fields.
        product = new Product();
        // Establecer el ID usando ReflectionTestUtils, ya que no hay setter público para id.
        ReflectionTestUtils.setField(product, "id", 1L);
        product.setName("  <b>Test Product</b> ");
        product.setDescription("   <i>Test Description</i>  ");
        product.setQuantity(100);
        product.setPrice(BigDecimal.valueOf(50.0));
    }

    /**
     * Test that adding a product sanitizes text fields and saves the product successfully.
     */
    @Test
    void testAddProduct_Success() {
        // Simulate the save operation assigning an ID to the product.
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 1L);
            return saved;
        });

        // Call the addProduct method.
        Product result = inventoryService.addProduct(product);

        // Verify that the fields were sanitized.
        assertEquals("Test Product", result.getName());
        assertEquals("Test Description", result.getDescription());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    /**
     * Test that adding a product with an empty (sanitized) name throws an IllegalArgumentException.
     */
    @Test
    void testAddProduct_InvalidData() {
        // Set an invalid product name (empty after sanitization).
        product.setName("  ");
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.addProduct(product));
        assertTrue(exception.getMessage().contains("Product name cannot be empty."));
    }

    /**
     * Test that updating a product sanitizes input and updates product fields correctly.
     */
    @Test
    void testUpdateProduct_Success() {
        // Simulate finding the existing product.
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product updatedData = new Product();
        updatedData.setName("  <b>Updated Product</b> ");
        updatedData.setDescription(" <i>Updated Description</i> ");
        updatedData.setQuantity(200);
        updatedData.setPrice(BigDecimal.valueOf(75.0));

        // Call updateProduct.
        Product result = inventoryService.updateProduct(product.getId(), updatedData);

        // Verify that the product fields have been sanitized and updated.
        assertEquals("Updated Product", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(200, result.getQuantity());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    /**
     * Test that an inventory movement of type "ENTRADA" is recorded correctly and adjusts the stock.
     */
    @Test
    void testAddMovement_Success() {
        // Crear un usuario y establecer su ID usando ReflectionTestUtils.
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        user.setEmail("user@test.com");

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(movementRepository.save(any(InventoryMovement.class))).thenAnswer(invocation -> {
            InventoryMovement movement = invocation.getArgument(0);
            ReflectionTestUtils.setField(movement, "id", 1L);
            return movement;
        });

        // Call addMovement for an "ENTRADA" (stock addition).
        InventoryMovement movement = inventoryService.addMovement(product.getId(), 10, "ENTRADA", "Restock", "No comment", user);

        // Verify that the movement is recorded and the product quantity is increased.
        assertNotNull(movement);
        // Suponiendo que setType ahora espera un enum, el método toString() debe devolver "ENTRADA".
        assertEquals("ENTRADA", movement.getType().toString());
        assertEquals(110, product.getQuantity());
        verify(productRepository, times(1)).save(product);
        verify(movementRepository, times(1)).save(any(InventoryMovement.class));
    }

    /**
     * Test that attempting a "SALIDA" movement with insufficient stock throws an IllegalArgumentException.
     */
    @Test
    void testAddMovement_InsufficientStock() {
        product.setQuantity(5);
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        user.setEmail("user@test.com");

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> inventoryService.addMovement(product.getId(), 10, "SALIDA", "Sale", "No comment", user));
        assertTrue(exception.getMessage().contains("Insufficient stock for removal."));
    }
}
