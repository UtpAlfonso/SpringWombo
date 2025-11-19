package com.jugueteria.api.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private String nombreUsuario; // Denormalizado para eficiencia
    private int calificacion;
    private String comentario;
    private LocalDateTime createdAt;
    private String productoNombre;
    private String productoImageUrl;
}