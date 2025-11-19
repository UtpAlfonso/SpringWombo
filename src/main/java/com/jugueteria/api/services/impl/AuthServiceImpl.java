package com.jugueteria.api.services.impl;
import com.jugueteria.api.dto.request.LoginRequest;
import com.jugueteria.api.dto.request.RegisterRequest;
import com.jugueteria.api.dto.response.AuthResponse;
import com.jugueteria.api.entity.Role;
import com.jugueteria.api.entity.Usuario;
import com.jugueteria.api.repository.RoleRepository;
import com.jugueteria.api.repository.UsuarioRepository;
import com.jugueteria.api.security.JwtService;
import com.jugueteria.api.services.AuthService;
import com.jugueteria.api.services.EmailService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.jugueteria.api.exception.ResourceNotFoundException; // Importa tu excepción personalizada
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
import java.time.LocalDateTime;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
private final UsuarioRepository usuarioRepository;
private final RoleRepository roleRepository;
private final PasswordEncoder passwordEncoder;
private final JwtService jwtService;
private final AuthenticationManager authenticationManager;
 private final EmailService emailService;
 private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

  @Override
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("Intento de registro con email duplicado: {}", request.getEmail());
            throw new IllegalArgumentException("El email ya está registrado.");
        }

        logger.info("Buscando el rol ROLE_CLIENT para el nuevo usuario.");
        Role userRole = roleRepository.findByNombre("ROLE_CLIENT")
                .orElseThrow(() -> {
                    // Este log es crucial para la depuración
                    logger.error("¡CONFIGURACIÓN INCORRECTA! El rol por defecto 'ROLE_CLIENT' no existe en la base de datos.");
                    return new IllegalStateException("Error de configuración interna del sistema.");
                });
        
        logger.info("Rol encontrado. Creando nuevo usuario con email: {}", request.getEmail());

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .build();
        
        usuarioRepository.save(usuario);
        logger.info("Usuario con email {} guardado exitosamente.", request.getEmail());

        var jwtToken = jwtService.generateToken(usuario); 
        return AuthResponse.builder().token(jwtToken).build();
    }

@Override
public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();

        // ¡Y AQUÍ TAMBIÉN!
        // Llamamos al método que acepta la entidad Usuario completa
        var jwtToken = jwtService.generateToken(user); 
        return AuthResponse.builder().token(jwtToken).build();
}
    @Override
    @Transactional
    public void processForgotPassword(String email) {
        // Buscamos al usuario por su email
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            // Generamos un token único y seguro
            String token = UUID.randomUUID().toString();
            
            // Guardamos el token y su fecha de expiración (ej. 1 hora) en la base de datos
            usuario.setResetPasswordToken(token);
            usuario.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1));
            
            usuarioRepository.save(usuario);
            
            // Enviamos el correo con el enlace de reseteo
            emailService.sendPasswordResetEmail(usuario, token);
        });
        // IMPORTANTE: No lanzamos un error si el email no existe.
        // Esto previene que alguien pueda usar este endpoint para averiguar qué emails están registrados.
    }

    @Override
    @Transactional
    public void processResetPassword(String token, String newPassword) {
        // Buscamos al usuario por el token de reseteo
        Usuario usuario = usuarioRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token de reseteo inválido."));
        
        // Verificamos si el token ha expirado
        if (usuario.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El token de reseteo ha expirado.");
        }
        
        // Actualizamos la contraseña
        usuario.setPassword(passwordEncoder.encode(newPassword));
        
        // Limpiamos los campos del token para que no se pueda volver a usar
        usuario.setResetPasswordToken(null);
        usuario.setResetPasswordTokenExpiry(null);
        
        usuarioRepository.save(usuario);
    }
}