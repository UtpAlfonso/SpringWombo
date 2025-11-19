package com.jugueteria.api.config;

import com.jugueteria.api.entity.Role;
import com.jugueteria.api.entity.Usuario;
import com.jugueteria.api.repository.RoleRepository;
import com.jugueteria.api.repository.UsuarioRepository;
import com.jugueteria.api.services.FileStorageService;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
     private final FileStorageService fileStorageService;

    @Override
    @Transactional // Agregamos transaccionalidad a todo el método run
    public void run(String... args) throws Exception {

        System.out.println("Inicializando carpeta de almacenamiento de archivos...");
        fileStorageService.init();
        // --- 1. Crear Roles si no existen ---
        if (roleRepository.findByNombre("ROLE_ADMIN").isEmpty()) {
            roleRepository.save(new Role("ROLE_ADMIN"));
        }
        if (roleRepository.findByNombre("ROLE_WORKER").isEmpty()) {
            roleRepository.save(new Role("ROLE_WORKER"));
        }
        if (roleRepository.findByNombre("ROLE_CLIENT").isEmpty()) {
            roleRepository.save(new Role("ROLE_CLIENT"));
        }

        // --- 2. Crear Usuario Administrador Base si no existe ---
        if (usuarioRepository.findByEmail("admin@jugueteria.com").isEmpty()) {
            System.out.println("Creando usuario administrador base...");

            // Obtenemos las referencias a los roles que SABEMOS que ya existen
            Role adminRole = roleRepository.findByNombre("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Error: Rol ROLE_ADMIN no encontrado."));
            Role workerRole = roleRepository.findByNombre("ROLE_WORKER")
                    .orElseThrow(() -> new RuntimeException("Error: Rol ROLE_WORKER no encontrado."));
            
            // Creamos el usuario SIN los roles primero
            Usuario adminUser = Usuario.builder()
                    .nombre("Administrador")
                    .apellido("Principal")
                    .email("admin@jugueteria.com")
                    .password(passwordEncoder.encode("admin1234567"))
                    .build();
            
            // Asignamos el conjunto de roles al usuario
            adminUser.setRoles(Set.of(adminRole, workerRole));
            
            // Guardamos el usuario con sus relaciones ya establecidas
            usuarioRepository.save(adminUser);
            
            System.out.println("Usuario administrador creado con email: admin@jugueteria.com y pass: admin123");
        }
    }
}