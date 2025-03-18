package com.myBusiness.service;

import com.myBusiness.dto.AuthResponse;
import com.myBusiness.dto.LoginRequest;
import com.myBusiness.dto.RegisterRequest;
import com.myBusiness.dto.UpdateUserRequest;

/**
 * AuthService interface defines the contract for authentication-related operations.
 * Implementations of this service should handle user registration, authentication,
 * user updates, deletion, token renewal, and logout operations.
 *
 * <p>Methods include:
 * <ul>
 *   <li>register: To create a new user account.</li>
 *   <li>authenticate: To verify user credentials and issue JWT tokens.</li>
 *   <li>updateUser: To update user details with appropriate authorization.</li>
 *   <li>deleteUser: To delete a user account, subject to authorization.</li>
 *   <li>renewAccessToken: To generate a new access token using a valid refresh token.</li>
 *   <li>logout: To invalidate the refresh token upon user logout.</li>
 * </ul>
 * </p>
 */
public interface AuthService {

    /**
     * Registers a new user using the provided registration details.
     *
     * @param request the registration details.
     * @throws IllegalArgumentException if the registration details are invalid.
     */
    void register(RegisterRequest request);

    /**
     * Authenticates a user using the provided login request details.
     *
     * @param request the login request details containing email and password.
     * @return an {@link AuthResponse} containing JWT tokens if authentication is successful.
     * @throws IllegalArgumentException if the credentials are invalid.
     */
    AuthResponse authenticate(LoginRequest request);

    /**
     * Updates the user details for the given user ID.
     * This operation requires the current user to have the necessary authorization.
     *
     * @param id the unique identifier of the user to update.
     * @param request the details to update.
     * @param currentUserRole the role of the current authenticated user performing the update.
     * @throws IllegalArgumentException if the provided details are invalid.
     * @throws SecurityException if the current user is not authorized to perform this update.
     */
    void updateUser(Long id, UpdateUserRequest request, String currentUserRole);

    /**
     * Deletes the user with the specified ID.
     * This operation requires the current user to have the necessary authorization.
     *
     * @param id the unique identifier of the user to delete.
     * @param currentUserRole the role of the current authenticated user performing the deletion.
     * @throws SecurityException if the current user is not authorized to delete this user.
     */
    void deleteUser(Long id, String currentUserRole);

    /**
     * Renews the access token using the provided refresh token.
     *
     * @param refreshToken the refresh token used to renew the access token.
     * @return the new access token.
     * @throws IllegalArgumentException if the refresh token is invalid or expired.
     */
    String renewAccessToken(String refreshToken);

    /**
     * Logs out the user by invalidating the provided refresh token.
     *
     * @param refreshToken the refresh token to invalidate.
     * @throws IllegalArgumentException if the refresh token is invalid.
     */
    void logout(String refreshToken);
}
