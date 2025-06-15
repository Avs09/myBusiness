package com.myBusiness.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.math.BigDecimal;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_created_date", columnList = "created_date")
    }
)
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "threshold_min", nullable = false)
    private Integer thresholdMin;

    @Column(name = "threshold_max", nullable = false)
    private Integer thresholdMax;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    // --- Auditoría ---
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private Instant createdDate;

    @LastModifiedBy
    @Column(name = "modified_by")
    private String modifiedBy;

    @LastModifiedDate
    @Column(name = "modified_date")
    private Instant modifiedDate;

    @PrePersist
    void onCreate() {
        validateName();
        validateThresholds();
        validatePrice();
        this.createdDate = this.modifiedDate = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        validateName();
        validateThresholds();
        validatePrice();
        this.modifiedDate = Instant.now();
    }

    public void updateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        this.name = name.trim();
    }

    public void updateThresholds(int min, int max) {
        if (min < 0 || max < 0 || min > max) {
            throw new IllegalArgumentException("Invalid thresholds.");
        }
        this.thresholdMin = min;
        this.thresholdMax = max;
    }

    public void updatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be null or negative.");
        }
        this.price = price;
    }

    private void validateName() {
        if (this.name == null || this.name.trim().isEmpty()) {
            throw new IllegalStateException("Product name must be defined.");
        }
    }

    private void validateThresholds() {
        if (this.thresholdMin == null || this.thresholdMax == null
            || this.thresholdMin < 0 || this.thresholdMax < 0
            || this.thresholdMin > this.thresholdMax) {
            throw new IllegalStateException("Thresholds are not valid.");
        }
    }

    private void validatePrice() {
        if (this.price == null || this.price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Price cannot be null or negative.");
        }
    }

    // Método reduceStock eliminado para no confundir: el stock real se calcula en use cases.
}
