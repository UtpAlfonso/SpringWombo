package com.jugueteria.api.dto.request;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class PaymentRequest {
    private BigDecimal amount;
    private String cardToken;
    private Integer installments;
    private String paymentMethodId;
    private String payerEmail;
}