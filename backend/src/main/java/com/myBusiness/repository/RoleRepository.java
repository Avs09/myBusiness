package com.myBusiness.repository;

import com.myBusiness.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for Role entities.
 * Provides methods to perform CRUD operations and to retrieve roles by their name.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a role by its unique name.
     *
     * @param name the unique name of the role (e.g., ROLE_ADMIN, ROLE_USER).
     * @return an Optional containing the Role if found, or empty if not found.
     */
    Optional<Role> findByName(String name);
}
