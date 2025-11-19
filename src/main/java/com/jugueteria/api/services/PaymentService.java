package com.jugueteria.api.services;
import com.jugueteria.api.entity.Usuario;

public interface PaymentService {
    String createPaymentPreference(Usuario usuario);
}