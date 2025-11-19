package com.jugueteria.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.util.List;

@Data
public class ReturnRequest {
    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;

    @NotEmpty(message = "Debe haber al menos un item para devolver")
    @Valid
    private List<ReturnItemRequest> items;

    private boolean devolverAlStock; // Checkbox para decidir si se reabastece el inventario

    @Data
    public static class ReturnItemRequest {
        @NotNull
        private Long detallePedidoId;

        @NotNull
        @Positive(message = "La cantidad a devolver debe ser mayor que cero")
        private int cantidadADevolver;

        private String motivo;
    }
}