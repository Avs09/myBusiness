package com.myBusiness.controller;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.myBusiness.model.User;
import com.myBusiness.repository.UserRepository;

/**
 * UserController exposes endpoints for user management.
 * It includes an endpoint to retrieve the current authenticated user's information.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;

    /**
     * Constructor for dependency injection.
     *
     * @param userRepository Repository for user persistence operations.
     */
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Endpoint to retrieve the current authenticated user's information.
     *
     * @param authentication Injected by Spring Security, containing user credentials.
     * @return ResponseEntity with the user details.
     * @throws EntityNotFoundException if the user is not found in the repository.
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        logger.info("Fetching user details for email: {}", email);
        User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return ResponseEntity.ok(user);
    }
}
