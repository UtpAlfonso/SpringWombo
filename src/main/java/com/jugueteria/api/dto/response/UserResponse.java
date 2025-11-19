package com.jugueteria.api.dto.response;

import lombok.Data;
import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private Set<String> roles; // Se envían los nombres de los roles, no los objetos completos
}