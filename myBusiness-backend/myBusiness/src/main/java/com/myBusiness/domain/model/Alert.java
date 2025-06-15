// src/main/java/com/myBusiness/domain/model/Alert.java
package com.myBusiness.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movement_id")
    private InventoryMovement movement;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 20)
    private AlertType alertType;   // UNDERSTOCK, OVERSTOCK

    @Column(name = "triggered_at", nullable = false, updatable = false)
    private Instant triggeredAt;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "created_by", length = 50)
    private String createdBy;
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate;
    @Column(name = "modified_by", length = 50)
    private String modifiedBy;
    @Column(name = "modified_date", nullable = false)
    private Instant modifiedDate;

    @PrePersist
    void onCreate() {
        this.triggeredAt = this.createdDate = this.modifiedDate = Instant.now();
        this.isRead = false;
    }

    @PreUpdate
    void onUpdate() {
        this.modifiedDate = Instant.now();
    }
}
