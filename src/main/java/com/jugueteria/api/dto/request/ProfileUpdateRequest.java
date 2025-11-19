package com.jugueteria.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileUpdateRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    private String apellido;

    // La contraseña es opcional. Si se envía, se actualiza.
    @Size(min = 6, message = "La nueva contraseña debe tener al menos 6 caracteres")
    private String password;
}