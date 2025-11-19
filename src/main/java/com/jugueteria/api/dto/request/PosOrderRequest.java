package com.jugueteria.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PosOrderRequest {

    @NotEmpty
    @Valid
    private List<PosOrderItem> items;

    @NotNull
    @Positive
    private BigDecimal total;

    @Data
    public static class PosOrderItem {
        @NotNull
        private Long productoId;

        @NotNull
        @Positive
        private int quantity;
    }
}