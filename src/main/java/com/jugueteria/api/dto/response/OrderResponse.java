package com.jugueteria.api.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private LocalDateTime fechaPedido;
    private String estado;
    private BigDecimal total;
    private String direccionEnvio;
    private List<OrderDetailResponse> detalles;
}