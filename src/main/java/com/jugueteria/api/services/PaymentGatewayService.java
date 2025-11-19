package com.jugueteria.api.services;
import com.jugueteria.api.dto.request.PaymentRequest;
import com.jugueteria.api.dto.response.PaymentResponse;
public interface PaymentGatewayService {
    PaymentResponse processPayment(PaymentRequest request);
}