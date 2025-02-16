package com.myBusiness.service;

import com.myBusiness.dto.AuthResponse;
import com.myBusiness.dto.LoginRequest;
import com.myBusiness.dto.RegisterRequest;
import com.myBusiness.dto.UpdateUserRequest;

public interface AuthService {
    void register(RegisterRequest request);

    AuthResponse authenticate(LoginRequest request);

    void updateUser(Long id, UpdateUserRequest request, String currentUserRole);

    void deleteUser(Long id, String currentUserRole);

    String renewAccessToken(String refreshToken);
}
