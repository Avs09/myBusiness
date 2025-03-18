package com.myBusiness.service.impl;

import com.myBusiness.dto.LoginRequest;
import com.myBusiness.dto.RegisterRequest;
import com.myBusiness.dto.UpdateUserRequest;
import com.myBusiness.model.Role;
import com.myBusiness.model.User;
import com.myBusiness.repository.RefreshTokenRepository;
import com.myBusiness.repository.RoleRepository;
import com.myBusiness.repository.UserRepository;
import com.myBusiness.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * AuthServiceImplTest contains unit tests for the AuthServiceImpl class.
 * It verifies that user registration, authentication, and administrative actions
 * behave correctly under various conditions.
 */
@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private Role userRole;

    @BeforeEach
    void setUp() {
        // Initialize a sample user and default role.
        user = new User();
        // Set the ID using ReflectionTestUtils because no setter is available.
        ReflectionTestUtils.setField(user, "id", 1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        userRole = new Role();
        ReflectionTestUtils.setField(userRole, "id", 1L);
        userRole.setName("USER");
    }

    /**
     * Test successful registration of a new user.
     */
    @Test
    void testRegister_Success() {
        // Instanciar el DTO usando el constructor parametrizado.
        RegisterRequest request = new RegisterRequest("newuser@example.com", "Password@123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("newuser@example.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertTrue(savedUser.getRoles().contains(userRole));
    }

    /**
     * Test that attempting to register with an existing email throws an exception.
     */
    @Test
    void testRegister_EmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("test@example.com", "Password@123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        assertTrue(exception.getMessage().contains("Email already in use."));
    }

    /**
     * Test successful user authentication returning access and refresh tokens.
     */
    @Test
    void testAuthenticate_Success() {
        // Usar el constructor parametrizado para LoginRequest.
        LoginRequest request = new LoginRequest("test@example.com", "rawPassword");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        // Simular dos llamadas a generateToken: primera para access token y segunda para refresh token.
        when(jwtUtil.generateToken(user.getEmail())).thenReturn("accessToken", "refreshToken");

        var authResponse = authService.authenticate(request);

        assertNotNull(authResponse);
        assertEquals("accessToken", authResponse.getAccessToken());
        assertEquals("refreshToken", authResponse.getRefreshToken());
    }

    /**
     * Test that authentication with an incorrect password throws an exception.
     */
    @Test
    void testAuthenticate_InvalidPassword() {
        LoginRequest request = new LoginRequest("test@example.com", "wrongPassword");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> authService.authenticate(request));
        assertTrue(exception.getMessage().contains("Invalid credentials."));
    }

    /**
     * Test that updating a user without ADMIN privileges results in an AccessDeniedException.
     */
    @Test
    void testUpdateUser_AccessDenied() {
        // Usar constructor parametrizado para UpdateUserRequest.
        UpdateUserRequest request = new UpdateUserRequest("updated@example.com", Collections.singleton(1L));

        Exception exception = assertThrows(AccessDeniedException.class, () -> authService.updateUser(1L, request, "USER"));
        assertTrue(exception.getMessage().contains("You do not have permission"));
    }

    /**
     * Test that deleting a user without ADMIN privileges results in an AccessDeniedException.
     */
    @Test
    void testDeleteUser_AccessDenied() {
        Exception exception = assertThrows(AccessDeniedException.class, () -> authService.deleteUser(1L, "USER"));
        assertTrue(exception.getMessage().contains("You do not have permission"));
    }

    /**
     * Test successful update of a user when performed by an ADMIN.
     */
    @Test
    void testUpdateUser_Success() {
        UpdateUserRequest request = new UpdateUserRequest("updated@example.com", Collections.singleton(1L));

        User existingUser = new User();
        ReflectionTestUtils.setField(existingUser, "id", 1L);
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("encodedPassword");
        existingUser.setRoles(Collections.singleton(userRole));

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(roleRepository.findAllById(request.getRoles())).thenReturn(Collections.singletonList(userRole));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        authService.updateUser(1L, request, "ADMIN");

        verify(userRepository).save(existingUser);
        assertEquals("updated@example.com", existingUser.getEmail());
    }

    /**
     * Test successful deletion of a user when performed by an ADMIN.
     */
    @Test
    void testDeleteUser_Success() {
        User existingUser = new User();
        ReflectionTestUtils.setField(existingUser, "id", 1L);
        existingUser.setEmail("delete@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        authService.deleteUser(1L, "ADMIN");

        verify(userRepository).delete(existingUser);
    }
}
