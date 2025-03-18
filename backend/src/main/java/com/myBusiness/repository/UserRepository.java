package com.myBusiness.repository;

import com.myBusiness.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for User entities.
 * Provides methods for user lookup and existence checks by email.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by email.
     *
     * @param email the email of the user.
     * @return an Optional containing the found user or empty if not found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given email.
     *
     * @param email the email to check.
     * @return true if a user with the given email exists, false otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Finds a user by email, ignoring case sensitivity.
     *
     * @param email the email of the user (case insensitive).
     * @return an Optional containing the found user or empty if not found.
     */
    Optional<User> findByEmailIgnoreCase(String email);
}
