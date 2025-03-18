package com.myBusiness.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

/**
 * RefreshToken is a JPA entity representing a refresh token.
 * It is linked to a user and contains token details and an expiry date.
 */
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One-to-one association with a User; each refresh token is linked to a single user.
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Unique token string used for refreshing the access token.
    @Column(nullable = false, unique = true)
    private String token;

    // The expiry date and time for the refresh token.
    @Column(nullable = false)
    private Instant expiryDate;

    /**
     * Default no-args constructor required by JPA.
     */
    public RefreshToken() {
    }

    /**
     * Constructor to initialize a RefreshToken with the given fields.
     *
     * @param user       the associated user.
     * @param token      the unique token string.
     * @param expiryDate the expiry date and time.
     */
    public RefreshToken(User user, String token, Instant expiryDate) {
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    /**
     * @return the refresh token's unique identifier.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the refresh token's unique identifier.
     *
     * @param id the identifier to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the associated user.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user associated with this token.
     *
     * @param user the user to set.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the token string.
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the token string.
     *
     * @param token the token to set.
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return the token expiry date.
     */
    public Instant getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the token expiry date.
     *
     * @param expiryDate the expiry date to set.
     */
    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RefreshToken)) return false;
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 0;
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "id=" + id +
                ", user=" + (user != null ? user.getId() : null) +
                ", token='" + token + '\'' +
                ", expiryDate=" + expiryDate +
                '}';
    }
}
