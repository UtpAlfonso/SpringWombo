package com.jugueteria.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "El SKU (Código de Referencia) es obligatorio")
    private String sku;

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio del producto es obligatorio")
    @Positive(message = "El precio debe ser un valor positivo mayor a cero")
    private BigDecimal precio;

    @NotNull(message = "El stock del producto es obligatorio")
    @PositiveOrZero(message = "El stock debe ser cero o un número positivo")
    private Integer stock;
    
    @NotNull(message = "El umbral de alerta de stock es obligatorio")
    @PositiveOrZero(message = "El umbral de alerta debe ser cero o un número positivo")
    private Integer umbralAlerta;

     private String imageUrl;
    @NotNull(message = "Necesita colocar una imagen del producto")

    @NotNull(message = "El ID de la categoría del producto es obligatorio")
    private Long categoriaId;

    // El proveedor es opcional, puede ser nulo
    private Long proveedorId;
}