package com.myBusiness.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

/**
 * InventoryMovement is a JPA entity that records changes in product inventory.
 * It logs both stock additions ("ENTRADA") and removals ("SALIDA"), along with details like date, reason, and comments.
 */
@Entity
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-one relationship linking the movement to a product
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Many-to-one relationship linking the movement to a user who performed the action
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // The quantity of product moved
    @Column(nullable = false)
    private Integer quantity;

    // Movement type indicating stock entry or exit; using enum for better type safety.
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MovementType type;

    // The date and time when the movement occurred
    @Column(nullable = false)
    private Instant date;

    // Reason for the movement (optional, up to 500 characters)
    @Column(length = 500)
    private String reason;

    // Additional comments (optional, up to 500 characters)
    @Column(length = 500)
    private String comment;

    /**
     * Default no-args constructor required by JPA.
     */
    public InventoryMovement() {
    }

    /**
     * Constructor with all fields (except the id).
     *
     * @param product  the associated product.
     * @param user     the user who performed the movement.
     * @param quantity the quantity moved.
     * @param type     the type of movement.
     * @param date     the date and time of the movement.
     * @param reason   the reason for the movement.
     * @param comment  additional comments.
     */
    public InventoryMovement(Product product, User user, Integer quantity, MovementType type, Instant date, String reason, String comment) {
        this.product = product;
        this.user = user;
        this.quantity = quantity;
        this.type = type;
        this.date = date;
        this.reason = reason;
        this.comment = comment;
    }

    // Getters and setters

    /**
     * @return the movement ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the movement ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the associated product.
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Sets the associated product.
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * @return the user who performed the movement.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who performed the movement.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the quantity moved.
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity moved.
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the movement type.
     */
    public MovementType getType() {
        return type;
    }

    /**
     * Sets the movement type.
     */
    public void setType(MovementType type) {
        this.type = type;
    }

    /**
     * @return the date of the movement.
     */
    public Instant getDate() {
        return date;
    }

    /**
     * Sets the date of the movement.
     */
    public void setDate(Instant date) {
        this.date = date;
    }

    /**
     * @return the reason for the movement.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the reason for the movement.
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return any additional comments.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets additional comments.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Enum representing the type of inventory movement.
     */
    public enum MovementType {
        ENTRADA,
        SALIDA
    }

    @Override
    public String toString() {
        return "InventoryMovement{" +
                "id=" + id +
                ", product=" + (product != null ? product.getId() : null) +
                ", user=" + (user != null ? user.getId() : null) +
                ", quantity=" + quantity +
                ", type=" + type +
                ", date=" + date +
                ", reason='" + reason + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventoryMovement)) return false;
        InventoryMovement that = (InventoryMovement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
