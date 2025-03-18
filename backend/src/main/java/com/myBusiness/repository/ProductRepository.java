package com.myBusiness.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.myBusiness.model.Product;

/**
 * Repository interface for Product entities.
 * Provides additional query methods to search by name, price range, and stock quantity.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Finds a product by its unique name.
     *
     * @param name the unique product name.
     * @return an Optional containing the found product or empty if not found.
     */
    Optional<Product> findByName(String name);
    
    /**
     * Retrieves products with prices within a specific range.
     *
     * @param minPrice the minimum price.
     * @param maxPrice the maximum price.
     * @param pageable pagination information.
     * @return a page of products within the given price range.
     */
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Searches for products whose names contain the specified keyword, ignoring case.
     *
     * @param name the keyword to search for in product names.
     * @param pageable pagination information.
     * @return a page of products matching the search criteria.
     */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * Finds all products with quantity less than the specified amount.
     *
     * @param quantity the threshold quantity.
     * @return a list of products with stock less than the given quantity.
     */
    List<Product> findByQuantityLessThan(int quantity);
}
