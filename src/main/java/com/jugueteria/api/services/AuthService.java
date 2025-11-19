package com.jugueteria.api.services;
import com.jugueteria.api.dto.request.LoginRequest;
import com.jugueteria.api.dto.request.RegisterRequest;
import com.jugueteria.api.dto.response.AuthResponse;
public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void processForgotPassword(String email);
    void processResetPassword(String token, String newPassword);
}