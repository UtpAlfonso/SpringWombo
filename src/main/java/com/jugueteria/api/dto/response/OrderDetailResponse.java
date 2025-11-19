package com.jugueteria.api.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderDetailResponse {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private int cantidad;
    private BigDecimal precioUnitario;
}