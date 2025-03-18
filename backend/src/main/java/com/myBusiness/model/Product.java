package com.myBusiness.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Product is a JPA entity representing a product in the inventory.
 * It includes details such as name, description, quantity, and price.
 * The @Version field is used for optimistic locking to handle concurrent updates.
 */
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique product name used to identify the product
    @Column(nullable = false, unique = true)
    private String name;

    // Detailed description of the product
    @Column(nullable = false)
    private String description;

    // Available quantity in stock
    @Column(nullable = false)
    private Integer quantity;

    // Price of the product
    @Column(nullable = false)
    private BigDecimal price;

    // Version field for optimistic locking
    @Version
    private Integer version;

    /**
     * Default no-args constructor required by JPA.
     */
    public Product() {
    }

    /**
     * Parameterized constructor to create a new Product instance.
     *
     * @param name        the product name.
     * @param description the product description.
     * @param quantity    the available quantity.
     * @param price       the product price.
     */
    public Product(String name, String description, Integer quantity, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and setters

    /**
     * @return the product's unique identifier.
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the product name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the product name.
     *
     * @param name the new product name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the product description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the product description.
     *
     * @param description the new product description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the available quantity.
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the available quantity.
     *
     * @param quantity the new available quantity.
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the product price.
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the product price.
     *
     * @param price the new product price.
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * @return the version number used for optimistic locking.
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * Sets the version number used for optimistic locking.
     *
     * @param version the new version number.
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        // Se utiliza el id para comparar; si es nulo, se considera que no hay igualdad.
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        // Si el id es nulo, se devuelve un valor constante.
        return id != null ? Objects.hash(id) : 0;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", version=" + version +
                '}';
    }
}
