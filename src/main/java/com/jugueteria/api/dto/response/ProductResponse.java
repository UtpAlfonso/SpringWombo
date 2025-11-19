package com.jugueteria.api.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductResponse {
    private Long id;
    private String sku;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private String imageUrl;
    private String categoriaNombre; // Se aplana la información para facilidad de uso
    private String proveedorNombre; // Se aplana la información
}