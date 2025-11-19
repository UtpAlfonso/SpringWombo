package com.jugueteria.api.controller;

import com.jugueteria.api.dto.request.ProfileUpdateRequest;
import com.jugueteria.api.dto.request.UserCreateRequest;
import com.jugueteria.api.dto.request.UserUpdateRequest;
import com.jugueteria.api.dto.response.UserResponse;
import com.jugueteria.api.entity.Usuario;
import com.jugueteria.api.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse createdUser = userService.createUser(request);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserResponse updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(userService.getProfile(usuario));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()") // Cualquier usuario autenticado puede editar su perfil
    public ResponseEntity<UserResponse> updateMyProfile(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody ProfileUpdateRequest request
    ) {
        UserResponse updatedProfile = userService.updateProfile(usuario, request);
        return ResponseEntity.ok(updatedProfile);
    }
}