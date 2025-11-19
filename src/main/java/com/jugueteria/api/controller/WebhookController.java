package com.jugueteria.api.controller;

import com.jugueteria.api.services.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;

    /**
     * Endpoint para recibir notificaciones de Mercado Pago.
     * @param notification El cuerpo de la notificación enviado por Mercado Pago.
     * @return una respuesta HTTP 200 OK para confirmar la recepción.
     */
    @PostMapping("/mercadopago")
public ResponseEntity<Void> handleMercadoPagoNotification(@RequestBody Map<String, Object> notification) {
    System.out.println("\n\n=====================================================");
    System.out.println(">>> ¡WEBHOOK DE MERCADO PAGO RECIBIDO! <<<");
    System.out.println("=====================================================");
    System.out.println("CUERPO DE LA NOTIFICACIÓN: " + notification);
    
    webhookService.processMercadoPagoNotification(notification);
    
    return ResponseEntity.ok().build();
}
}