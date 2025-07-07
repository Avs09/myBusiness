package com.myBusiness.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "alerts")
@EntityListeners(AuditingEntityListener.class)         // <— para @CreatedBy/@LastModifiedBy
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // relación con Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movement_id")
    private InventoryMovement movement;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 20)
    private AlertType alertType;

    @Column(name = "triggered_at", nullable = false, updatable = false)
    private Instant triggeredAt;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    // campos de auditoría
    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 50)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate;

    @LastModifiedBy
    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @LastModifiedDate
    @Column(name = "modified_date", nullable = false)
    private Instant modifiedDate;

    @PrePersist
    void prePersist() {
        this.triggeredAt = Instant.now();
        this.isRead = false;
    }
}
