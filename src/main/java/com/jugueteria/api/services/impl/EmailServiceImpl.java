package com.jugueteria.api.services.impl;

import com.jugueteria.api.entity.Pedido;
import com.jugueteria.api.entity.Usuario;
import com.jugueteria.api.services.EmailService;
import com.resend.Resend;
import com.resend.core.exception.ResendException;

import com.resend.services.emails.model.Attachment;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.RequiredArgsConstructor; // Opcional, ya que usaremos @Value
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.Base64;

@Service
// Quitamos @RequiredArgsConstructor porque inyectaremos valores del properties con @Value
public class EmailServiceImpl implements EmailService {

    @Value("${resend.api.key}")
    private String apiKey;

    @Value("${resend.from.email}")
    private String fromEmail; // Ej: onboarding@resend.dev

    @Override
    @Async // Sigue siendo asíncrono para no trabar la venta
    public void sendOrderConfirmationEmail(Pedido pedido) {
        try {
            // 1. Inicializar Resend
            Resend resend = new Resend(apiKey);

            // 2. Construir tu HTML (Tu lógica original intacta)
            String htmlMsg = "<h3>¡Gracias por tu compra, " + pedido.getCliente().getNombre() + "!</h3>"
                    + "<p>Tu pedido con ID #" + pedido.getId() + " ha sido recibido y está siendo procesado.</p>"
                    + "<p><b>Total:</b> S/. " + pedido.getTotal() + "</p>"
                    + "<p><b>Dirección de Envío:</b> " + pedido.getDireccionEnvio() + "</p>"
                    + "<p>Pronto recibirás otra notificación cuando tu pedido sea enviado.</p>"
                    + "<p>Saludos,<br>El equipo de Juguetería Fantasía</p>";

            // 3. Configurar el envío
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("Juguetería Fantasía <" + fromEmail + ">") // Remitente
                    .to(pedido.getCliente().getEmail())              // Destinatario
                    .subject("¡Confirmación de tu pedido #" + pedido.getId() + " en Juguetería Fantasía!")
                    .html(htmlMsg)
                    .build();

            // 4. Enviar (Viaja por HTTP puerto 443, Render NO lo bloquea)
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println("Email confirmación enviado. ID: " + data.getId());

        } catch (ResendException e) {
            System.err.println("Error al enviar el correo de confirmación: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado en email: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendOrderConfirmationWithInvoice(Pedido pedido, byte[] pdfBytes) {
        try {
            Resend resend = new Resend(apiKey);

            // 1. Convertir el PDF a Base64 (Requisito de la API de Resend)
            String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);

            // 2. Crear el objeto Attachment
            Attachment invoiceAttachment = Attachment.builder()
                    .fileName("Boleta_Pedido_" + pedido.getId() + ".pdf") 
                    .content(base64Pdf) // El contenido en base64
                    .build();

            // 3. Tu HTML de siempre
            String htmlMsg = "<h3>¡Gracias por tu compra, " + pedido.getCliente().getNombre() + "!</h3>"
                    + "<p>Tu pedido con ID #" + pedido.getId() + " ha sido confirmado.</p>"
                    + "<p>Adjunto encontrarás tu boleta de venta electrónica.</p>"
                    + "<p>Saludos,<br>El equipo de Juguetería Fantasía</p>";

            // 4. Configurar el correo con el adjunto (.attachments)
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("Juguetería Fantasía <" + fromEmail + ">")
                    .to(pedido.getCliente().getEmail())
                    .subject("Confirmación y Boleta de Pedido #" + pedido.getId())
                    .html(htmlMsg)
                    .attachments(invoiceAttachment) // <--- AQUÍ SE AGREGA
                    .build();

            CreateEmailResponse data = resend.emails().send(params);
            System.out.println("Email con boleta enviado. ID: " + data.getId());

        } catch (ResendException e) {
            System.err.println("Error Resend: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error general email: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPasswordResetEmail(Usuario usuario, String token) {
        try {
            Resend resend = new Resend(apiKey);

            // Tu URL original
            String resetUrl = "https://womboangular.onrender.com/reset-password?token=" + token;

            // Tu HTML original
            String htmlMsg = "<h3>Hola, " + usuario.getNombre() + "</h3>"
                    + "<p>Hemos recibido una solicitud para restablecer tu contraseña. Haz clic en el siguiente enlace para continuar:</p>"
                    + "<a href=\"" + resetUrl + "\">Restablecer mi contraseña</a>"
                    + "<p>Si no solicitaste esto, puedes ignorar este correo.</p>"
                    + "<p>El enlace expirará en 1 hora.</p>";

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("Juguetería Fantasía <" + fromEmail + ">")
                    .to(usuario.getEmail())
                    .subject("Restablece tu contraseña en Juguetería Fantasía")
                    .html(htmlMsg)
                    .build();

            CreateEmailResponse data = resend.emails().send(params);
            System.out.println("Email recuperación enviado. ID: " + data.getId());

        } catch (ResendException e) {
            System.err.println("Error al enviar el correo de reseteo: " + e.getMessage());
        }
    }
}