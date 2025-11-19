package com.jugueteria.api.services;
import com.jugueteria.api.dto.response.UserResponse;
import com.jugueteria.api.dto.request.UserUpdateRequest;
import com.jugueteria.api.dto.request.ProfileUpdateRequest;
import com.jugueteria.api.dto.request.UserCreateRequest;
import com.jugueteria.api.entity.Usuario;
import java.util.List;
public interface UserService {
    List<UserResponse> findAll();
    UserResponse findById(Long id);
    UserResponse getProfile(Usuario usuario);
    UserResponse createUser(UserCreateRequest request);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    void deleteById(Long id);
    UserResponse updateProfile(Usuario usuario, ProfileUpdateRequest request);
}