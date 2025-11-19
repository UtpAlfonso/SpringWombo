package com.jugueteria.api.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class SalesReportResponse {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private long numeroPedidos;
    private BigDecimal totalVentas;
    // Aquí se podría añadir una lista de ventas por día, etc.
    private List<DailySaleDto> ventasPorDia; // Para el gráfico de barras/líneas
    private List<ProductSaleDto> topProductosVendidos; 
}