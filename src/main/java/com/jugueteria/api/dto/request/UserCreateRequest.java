package com.jugueteria.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

/**
 * DTO (Data Transfer Object) para recibir los datos necesarios
 * para crear un nuevo usuario desde el panel de administración.
 * Incluye validaciones para asegurar la integridad de los datos.
 */
@Data
public class UserCreateRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    private String apellido;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser una dirección de correo electrónico válida")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotEmpty(message = "Se debe asignar al menos un rol al usuario")
    private Set<String> roles; // Un conjunto de nombres de rol, ej: ["ROLE_ADMIN", "ROLE_WORKER"]
}