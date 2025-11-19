package com.jugueteria.api.services.impl;

import com.jugueteria.api.dto.response.UserResponse;
import com.jugueteria.api.entity.Usuario;
import com.jugueteria.api.entity.Role;
import com.jugueteria.api.exception.ResourceNotFoundException;
import com.jugueteria.api.repository.UsuarioRepository;
import com.jugueteria.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.jugueteria.api.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.jugueteria.api.dto.request.ProfileUpdateRequest;
import com.jugueteria.api.dto.request.UserCreateRequest;
import com.jugueteria.api.dto.request.UserUpdateRequest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;          // <-- DEPENDENCIA AÑADIDA
    private final PasswordEncoder passwordEncoder;        // <-- DEPENDENCIA AÑADIDA

    @Override
    public List<UserResponse> findAll() {
        return usuarioRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

     @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email '" + request.getEmail() + "' ya está en uso.");
        }

        Set<Role> roles = findRolesByNames(request.getRoles());

        Usuario newUser = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build();
        
        Usuario savedUser = usuarioRepository.save(newUser);
        return modelMapper.map(savedUser, UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        // Verificar si el nuevo email ya está en uso por OTRO usuario
        usuarioRepository.findByEmail(request.getEmail()).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(id)) {
                throw new IllegalArgumentException("El email '" + request.getEmail() + "' ya está en uso por otro usuario.");
            }
        });
        
        Set<Role> roles = findRolesByNames(request.getRoles());

        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setRoles(roles);

        // Actualizar la contraseña solo si se proporcionó una nueva
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        Usuario updatedUser = usuarioRepository.save(usuario);
        return modelMapper.map(updatedUser, UserResponse.class);
    }


    @Override
    public UserResponse findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return modelMapper.map(usuario, UserResponse.class);
    }

    @Override
    public UserResponse getProfile(Usuario usuario) {
        return modelMapper.map(usuario, UserResponse.class);
    }

    @Override
    public void deleteById(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar, usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    private Set<Role> findRolesByNames(Set<String> roleNames) {
        return roleNames.stream()
                .map(roleName -> roleRepository.findByNombre(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("El rol especificado no existe: " + roleName)))
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Usuario usuario, ProfileUpdateRequest request) {
        // El objeto 'usuario' ya es la entidad gestionada por JPA, obtenida de @AuthenticationPrincipal
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());

        // Actualizar contraseña solo si se proporcionó una nueva
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        Usuario updatedUser = usuarioRepository.save(usuario);
        return modelMapper.map(updatedUser, UserResponse.class);
    }
}
