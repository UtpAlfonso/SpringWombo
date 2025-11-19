package com.jugueteria.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor // Ahora generará un constructor (String, Long, BigDecimal)
public class ProductSaleDto {
    private String productName;
    private Long quantitySold; // <-- CAMBIADO DE 'int' A 'Long'
    private BigDecimal totalRevenue;
}