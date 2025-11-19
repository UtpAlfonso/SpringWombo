package com.jugueteria.api.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MercadoPagoPaymentRequest {
    @NotBlank private String token;
    @NotBlank private String paymentMethodId;
    @NotNull @Positive private Integer installments;
    @NotBlank @Email private String payerEmail;
}