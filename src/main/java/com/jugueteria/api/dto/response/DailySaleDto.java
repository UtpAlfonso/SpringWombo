package com.jugueteria.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date; // <-- CAMBIAR a java.util.Date

@Data
@AllArgsConstructor
@NoArgsConstructor // Es buena práctica añadir un constructor vacío
public class DailySaleDto {
    private Date date; // <-- CAMBIADO DE LocalDate a Date
    private BigDecimal total;
}