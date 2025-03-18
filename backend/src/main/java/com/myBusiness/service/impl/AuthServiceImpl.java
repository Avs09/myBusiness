package com.myBusiness.service.impl;

import com.myBusiness.dto.AuthResponse;
import com.myBusiness.dto.LoginRequest;
import com.myBusiness.dto.RegisterRequest;
import com.myBusiness.dto.UpdateUserRequest;
import com.myBusiness.model.RefreshToken;
import com.myBusiness.model.Role;
import com.myBusiness.model.User;
import com.myBusiness.repository.RefreshTokenRepository;
import com.myBusiness.repository.RoleRepository;
import com.myBusiness.repository.UserRepository;
import com.myBusiness.service.AuthService;
import com.myBusiness.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * AuthServiceImpl provides the implementation for authentication operations.
 * It handles user registration, authentication (with JWT), token renewal,
 * administrative user updates, deletions, and logout operations.
 *
 * Methods modifying multiple database records are annotated with @Transactional.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registers a new user after checking for duplicate emails.
     * This operation is transactional to ensure that the new user and its roles are saved atomically.
     *
     * @param request the registration details.
     * @throws IllegalArgumentException if the email is already in use or the required role is missing.
     */
    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration attempt with existing email: {}", request.getEmail());
            throw new IllegalArgumentException("Email already in use.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("User role not found."));
        user.setRoles(Collections.singleton(userRole));

        userRepository.save(user);
        logger.info("New user registered: {}", request.getEmail());
    }

    /**
     * Authenticates the user by validating credentials.
     * Generates an access token (JWT) and a refresh token upon successful authentication.
     * This operation is transactional to ensure consistent token generation and storage.
     *
     * @param request the login credentials.
     * @return an AuthResponse containing the access and refresh tokens.
     * @throws IllegalArgumentException if the credentials are invalid.
     */
    @Override
    @Transactional
    public AuthResponse authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Login attempt with unregistered email: {}", request.getEmail());
                    return new IllegalArgumentException("Invalid credentials.");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Invalid password for user: {}", request.getEmail());
            throw new IllegalArgumentException("Invalid credentials.");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        String refreshToken = generateRefreshToken(user);
        logger.info("User authenticated successfully: {}", request.getEmail());
        return new AuthResponse(token, refreshToken);
    }

    /**
     * Updates user details (email and roles) if the current user has ADMIN privileges.
     * This operation is transactional to ensure that all changes are applied atomically.
     *
     * @param id the identifier of the user to update.
     * @param request the updated user details.
     * @param currentUserRole the role of the current user performing the update.
     * @throws AccessDeniedException if the current user does not have ADMIN privileges.
     * @throws EntityNotFoundException if the user does not exist.
     */
    @Override
    @Transactional
    public void updateUser(Long id, UpdateUserRequest request, String currentUserRole) {
        if (!"ADMIN".equalsIgnoreCase(currentUserRole)) {
            logger.warn("Access denied: insufficient privileges to update user with ID: {}", id);
            throw new AccessDeniedException("You do not have permission to perform this action.");
        }

        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found."));

        user.setEmail(request.getEmail());
        if (request.getRoles() != null) {
            List<Role> roleList = roleRepository.findAllById(request.getRoles());
            Set<Role> roles = new HashSet<>(roleList);
            user.setRoles(roles);
        }

        userRepository.save(user);
        logger.info("User updated: ID {}, new email: {}", id, request.getEmail());
    }

    /**
     * Deletes a user if the current user has ADMIN privileges.
     * This operation is transactional to ensure the deletion is applied consistently.
     *
     * @param id the identifier of the user to delete.
     * @param currentUserRole the role of the current user performing the deletion.
     * @throws AccessDeniedException if the current user does not have ADMIN privileges.
     * @throws EntityNotFoundException if the user does not exist.
     */
    @Override
    @Transactional
    public void deleteUser(Long id, String currentUserRole) {
        if (!"ADMIN".equalsIgnoreCase(currentUserRole)) {
            logger.warn("Access denied: insufficient privileges to delete user with ID: {}", id);
            throw new AccessDeniedException("You do not have permission to perform this action.");
        }

        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found."));

        userRepository.delete(user);
        logger.info("User deleted: ID {}", id);
    }

    /**
     * Generates and stores a refresh token for the user.
     * The refresh token is valid for 7 days.
     * This operation is transactional to ensure token persistence is consistent.
     *
     * @param user the user for whom the refresh token is generated.
     * @return the generated refresh token string.
     */
    @Transactional
    public String generateRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(jwtUtil.generateToken(user.getEmail()));
        refreshToken.setExpiryDate(Instant.now().plusMillis(7 * 24 * 60 * 60 * 1000)); // 7 days validity

        refreshTokenRepository.save(refreshToken);
        logger.info("Refresh token generated for user: {}", user.getEmail());
        return refreshToken.getToken();
    }

    /**
     * Renews an access token using the provided refresh token.
     * Validates the refresh token before generating a new access token.
     * This operation is transactional to ensure that token validation and renewal occur atomically.
     *
     * @param refreshToken the refresh token used to renew the access token.
     * @return a new access token.
     * @throws IllegalArgumentException if the refresh token is invalid or expired.
     */
    @Override
    @Transactional
    public String renewAccessToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> {
                    logger.warn("Attempt to renew token with invalid refresh token: {}", refreshToken);
                    return new IllegalArgumentException("Invalid or expired refresh token.");
                });

        if (storedToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(storedToken);
            logger.warn("Expired refresh token deleted: {}", refreshToken);
            throw new IllegalArgumentException("Refresh token expired.");
        }

        User user = storedToken.getUser();
        logger.info("Access token renewed for user: {}", user.getEmail());
        return jwtUtil.generateToken(user.getEmail());
    }

    /**
     * Logs out the user by invalidating the provided refresh token.
     * This operation deletes the refresh token from the database.
     * This method is transactional to ensure that token invalidation is applied consistently.
     *
     * @param refreshToken the refresh token to invalidate.
     * @throws IllegalArgumentException if the refresh token is invalid or not found.
     */
    @Override
    @Transactional
    public void logout(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> {
                    logger.warn("Logout attempt with invalid refresh token: {}", refreshToken);
                    return new IllegalArgumentException("Invalid refresh token.");
                });
        refreshTokenRepository.delete(storedToken);
        logger.info("User logged out, refresh token invalidated: {}", refreshToken);
    }
}
