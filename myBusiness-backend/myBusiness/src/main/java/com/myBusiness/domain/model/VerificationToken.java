// src/main/java/com/myBusiness/domain/model/VerificationToken.java
package com.myBusiness.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "verification_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String token;            // código alfanumérico de 8 caracteres

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private boolean used;       

  
    @Column(name = "pending_email", nullable = false, length = 100)
    private String pendingEmail;

    @Column(name = "pending_name", nullable = false, length = 100)
    private String pendingName;

    @Column(name = "pending_password_hash", nullable = false, length = 255)
    private String pendingPasswordHash;

 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
