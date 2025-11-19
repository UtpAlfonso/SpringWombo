package com.jugueteria.api.services.impl;

import com.jugueteria.api.entity.Pedido;
import com.jugueteria.api.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.jugueteria.api.entity.Usuario;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    @Async // Se ejecuta en un hilo separado para no bloquear la respuesta al usuario
    public void sendOrderConfirmationEmail(Pedido pedido) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(pedido.getCliente().getEmail());
            helper.setSubject("¡Confirmación de tu pedido #" + pedido.getId() + " en Juguetería Fantasía!");

            // Construir el cuerpo del correo en HTML
            String htmlMsg = "<h3>¡Gracias por tu compra, " + pedido.getCliente().getNombre() + "!</h3>"
                    + "<p>Tu pedido con ID #" + pedido.getId() + " ha sido recibido y está siendo procesado.</p>"
                    + "<p><b>Total:</b> S/. " + pedido.getTotal() + "</p>"
                    + "<p><b>Dirección de Envío:</b> " + pedido.getDireccionEnvio() + "</p>"
                    + "<p>Pronto recibirás otra notificación cuando tu pedido sea enviado.</p>"
                    + "<p>Saludos,<br>El equipo de Juguetería Fantasía</p>";
            
            helper.setText(htmlMsg, true); // true indica que el texto es HTML

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // En un proyecto real, aquí se manejaría el error (ej. log, reintentos)
            System.err.println("Error al enviar el correo de confirmación: " + e.getMessage());
        }
    }
    
    @Override
    @Async
    public void sendPasswordResetEmail(Usuario usuario, String token) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(usuario.getEmail());
            helper.setSubject("Restablece tu contraseña en Juguetería Fantasía");

            // La URL debe apuntar a tu frontend, incluyendo el token
            String resetUrl = "http://localhost:4200/reset-password?token=" + token;

            String htmlMsg = "<h3>Hola, " + usuario.getNombre() + "</h3>"
                    + "<p>Hemos recibido una solicitud para restablecer tu contraseña. Haz clic en el siguiente enlace para continuar:</p>"
                    + "<a href=\"" + resetUrl + "\">Restablecer mi contraseña</a>"
                    + "<p>Si no solicitaste esto, puedes ignorar este correo.</p>"
                    + "<p>El enlace expirará en 1 hora.</p>";
            
            helper.setText(htmlMsg, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.err.println("Error al enviar el correo de reseteo: " + e.getMessage());
        }
    }
}