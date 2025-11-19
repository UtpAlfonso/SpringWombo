package com.jugueteria.api.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponse {
    private List<CartItemResponse> items;
    private BigDecimal total;
}