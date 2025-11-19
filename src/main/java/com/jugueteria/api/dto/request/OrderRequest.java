package com.jugueteria.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {
    @NotBlank private String direccionEnvio;
    @NotNull @Valid private MercadoPagoPaymentRequest paymentData;
}