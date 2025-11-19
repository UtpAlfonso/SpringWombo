package com.jugueteria.api.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Long productoId;
    private String productoNombre;
    private BigDecimal precioUnitario;
    private int cantidad;
    private BigDecimal subtotal;
    private String productoImageUrl;
    private int productoStock;
}