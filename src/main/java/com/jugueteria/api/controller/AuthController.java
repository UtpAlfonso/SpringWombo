package com.jugueteria.api.controller;

import com.jugueteria.api.dto.request.LoginRequest;
import com.jugueteria.api.dto.request.RegisterRequest;
import com.jugueteria.api.dto.response.AuthResponse;
import com.jugueteria.api.services.AuthService;
import com.jugueteria.api.dto.request.PasswordForgotRequest;
import com.jugueteria.api.dto.request.PasswordResetRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
     @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody PasswordForgotRequest request) {
        authService.processForgotPassword(request.getEmail());
        // Siempre devolvemos 200 OK para no revelar si un email existe o no
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        authService.processResetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}