package com.jugueteria.api.services;
import com.jugueteria.api.entity.Pedido;

public interface PdfService {
    byte[] generateOrderInvoicePdf(Pedido pedido);
}