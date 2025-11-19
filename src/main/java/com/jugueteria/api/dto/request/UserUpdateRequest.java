package com.jugueteria.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;

@Data
public class UserUpdateRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    private String apellido;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un formato de email válido")
    private String email;

    // La contraseña es opcional al actualizar.
    // Si se envía, debe tener al menos 6 caracteres.
    @Size(min = 6, message = "La nueva contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotEmpty(message = "Se debe asignar al menos un rol")
    private Set<String> roles;
}