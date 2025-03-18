package com.myBusiness.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * User is a JPA entity representing an application user.
 * It includes credentials and a set of roles for access control.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique email used as the username
    @Column(unique = true, nullable = false)
    private String email;

    // Encrypted user password (no se expone en toString)
    @Column(nullable = false)
    private String password;

    // Eagerly load roles; a user can have multiple roles in a many-to-many relationship
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * Protected no-args constructor required by JPA.
     */
    public User() {
    }

    /**
     * Constructor to create a new User.
     *
     * @param email    the user's email.
     * @param password the user's encrypted password.
     * @param roles    the set of roles associated with the user.
     */
    public User(String email, String password, Set<Role> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles != null ? roles : new HashSet<>();
    }

    /**
     * @return the user's unique identifier.
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     *
     * @param email the user's email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the user's encrypted password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's encrypted password.
     *
     * @param password the user's encrypted password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the set of roles associated with the user.
     */
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * Sets the roles for the user.
     *
     * @param roles the roles to set.
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                '}';
    }
}
