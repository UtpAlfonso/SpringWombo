
package com.jugueteria.api.services;
import com.jugueteria.api.entity.Pedido;
import com.jugueteria.api.entity.Usuario;

public interface EmailService {
    void sendOrderConfirmationEmail(Pedido pedido);
    void sendPasswordResetEmail(Usuario usuario, String token);
    void sendOrderConfirmationWithInvoice(Pedido pedido, byte[] pdfBytes);
    // void sendPasswordResetEmail(Usuario usuario, String token); // Para futura implementación
}