package com.jugueteria.api.services.impl;

import com.jugueteria.api.dto.response.DailySaleDto;
import com.jugueteria.api.dto.response.ProductSaleDto;
import com.jugueteria.api.dto.response.SalesReportResponse;
import com.jugueteria.api.entity.Pedido;
import com.jugueteria.api.repository.PedidoRepository;
import com.jugueteria.api.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

   private final PedidoRepository pedidoRepository;

    @Override
    public SalesReportResponse getSalesReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        List<String> excludedStatuses = List.of("PAGO_FALLIDO", "PENDIENTE", "CANCELADO");

        // 1. Obtener los datos agregados directamente de la base de datos
        List<DailySaleDto> ventasPorDia = pedidoRepository.findSalesByDay(startDateTime, endDateTime);
        List<ProductSaleDto> topProductos = pedidoRepository.findTopSellingProducts(startDateTime, endDateTime);
        
        // 2. Calcular los totales
        long numeroPedidos = pedidoRepository.countByFechaPedidoBetweenAndEstadoNotIn(startDateTime, endDateTime, excludedStatuses);
        BigDecimal totalVentas = ventasPorDia.stream()
                .map(DailySaleDto::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Construir el objeto de respuesta completo
        SalesReportResponse response = new SalesReportResponse();
        response.setFechaInicio(startDate);
        response.setFechaFin(endDate);
        response.setNumeroPedidos(numeroPedidos);
        response.setTotalVentas(totalVentas);
        response.setVentasPorDia(ventasPorDia);
        response.setTopProductosVendidos(topProductos);

        return response;
    }
}