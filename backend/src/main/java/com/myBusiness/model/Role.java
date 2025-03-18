package com.myBusiness.model;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Role is a JPA entity representing a user role in the system.
 * It stores a unique role name used for access control.
 */
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique name of the role (e.g., ROLE_ADMIN, ROLE_USER)
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * Default no-args constructor required by JPA.
     */
    public Role() {
    }

    /**
     * Constructor to create a new Role instance with the given name.
     *
     * @param name the name of the role.
     */
    public Role(String name) {
        this.name = name;
    }

    /**
     * @return the role's unique identifier.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the role's unique identifier.
     *
     * @param id the role id.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the name of the role.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the role.
     *
     * @param name the role name.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 0;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
