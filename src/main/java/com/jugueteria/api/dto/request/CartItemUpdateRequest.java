package com.jugueteria.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CartItemUpdateRequest {
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser al menos 1")
    private int cantidad;
}