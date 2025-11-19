package com.jugueteria.api.services.impl;

import com.jugueteria.api.services.OrderService;
import com.jugueteria.api.services.WebhookService;
import com.mercadopago.client.merchantorder.MerchantOrderClient;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.merchantorder.MerchantOrder;
import com.mercadopago.resources.merchantorder.MerchantOrderPayment;
import com.mercadopago.resources.payment.Payment;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookServiceImpl.class);
    private final OrderService orderService;

    /**
     * Procesa la notificación entrante de Mercado Pago.
     * Actúa como un despachador que identifica el tipo de notificación y delega
     * el procesamiento al método correspondiente.
     *
     * @param notification El cuerpo de la notificación en formato de Mapa.
     */
    @Override
    public void processMercadoPagoNotification(Map<String, Object> notification) {
        logger.info("[WebhookService] Iniciando procesamiento de notificación: {}", notification);
        
        // Determinar el tipo de notificación. 'topic' es el más nuevo, 'type' es un fallback.
        String topic = (String) notification.get("topic");
        if (topic == null) {
            topic = (String) notification.get("type");
        }

        if ("payment".equals(topic) || "payment.updated".equals(notification.get("action"))) {
            logger.info("[WebhookService] Detectada notificación de tipo 'payment'.");
            handlePaymentNotification(notification);
        } else if ("merchant_order".equals(topic)) {
            logger.info("[WebhookService] Detectada notificación de tipo 'merchant_order'.");
            handleMerchantOrderNotification(notification);
        } else {
            logger.warn("[WebhookService] Notificación ignorada: tipo desconocido -> {}", topic);
        }
    }

    /**
     * Procesa las notificaciones modernas de tipo 'merchant_order'.
     * Este es el flujo preferido y más robusto.
     *
     * @param notification La notificación recibida.
     */
    private void handleMerchantOrderNotification(Map<String, Object> notification) {
        String resourceUrl = (String) notification.get("resource");
        if (resourceUrl == null || resourceUrl.isEmpty()) {
            logger.error("[WebhookService] Error: Notificación 'merchant_order' sin 'resource' URL.");
            return;
        }

        try {
            // Extraer el ID de la Merchant Order desde la URL del recurso
            String[] urlParts = resourceUrl.split("/");
            Long merchantOrderId = Long.parseLong(urlParts[urlParts.length - 1]);
            logger.info("[WebhookService] ID de Merchant Order de MP extraído: {}", merchantOrderId);

            // Consultar la información completa de la Merchant Order a la API de Mercado Pago
            MerchantOrderClient client = new MerchantOrderClient();
            MerchantOrder orderInfo = client.get(merchantOrderId);

            // Una Merchant Order puede tener múltiples intentos de pago. Nos interesa el último pago aprobado.
            MerchantOrderPayment lastApprovedPayment = orderInfo.getPayments().stream()
                .filter(p -> "approved".equalsIgnoreCase(p.getStatus()))
                .reduce((first, second) -> second) // Obtener el último de la lista
                .orElse(null);

            if (lastApprovedPayment == null) {
                logger.info("[WebhookService] La Merchant Order {} no tiene pagos aprobados todavía.", merchantOrderId);
                return;
            }

            // Validar que tengamos la referencia a nuestro pedido interno
            if (orderInfo.getExternalReference() == null || orderInfo.getExternalReference().isEmpty()) {
                logger.error("[WebhookService] Error: La Merchant Order {} no tiene 'external_reference'.", merchantOrderId);
                return;
            }

            Long ourOrderId = Long.parseLong(orderInfo.getExternalReference());
            String paymentStatus = lastApprovedPayment.getStatus();

            logger.info("[WebhookService] Llamando a OrderService para actualizar el pedido #{} al estado: {}", ourOrderId, paymentStatus);
            orderService.updateOrderStatusFromWebhook(ourOrderId, paymentStatus);

        } catch (NumberFormatException e) {
            logger.error("[WebhookService] Error al parsear el ID desde la URL del recurso: {}", resourceUrl, e);
        } catch (MPException e) {
            logger.error("[WebhookService] Error de SDK/API de Mercado Pago al procesar 'merchant_order':", e);
        } catch (Exception e) {
            logger.error("[WebhookService] Error inesperado al procesar 'merchant_order':", e);
        }
    }

    /**
     * Procesa las notificaciones antiguas de tipo 'payment'.
     * Mantenido por compatibilidad.
     *
     * @param notification La notificación recibida.
     */
    private void handlePaymentNotification(Map<String, Object> notification) {
        Map<String, Object> data = (Map<String, Object>) notification.get("data");
        if (data == null || data.get("id") == null) {
            logger.error("[WebhookService] Error: Notificación 'payment' sin 'data.id'.");
            return;
        }
        
        Long paymentIdFromMP = Long.parseLong(data.get("id").toString());
        logger.info("[WebhookService] ID de pago de Mercado Pago extraído: {}", paymentIdFromMP);

        try {
            PaymentClient client = new PaymentClient();
            Payment paymentInfo = client.get(paymentIdFromMP);

            if (paymentInfo == null || paymentInfo.getExternalReference() == null || paymentInfo.getExternalReference().isEmpty()) {
                logger.error("[WebhookService] Error: El pago {} no tiene 'external_reference'.", paymentIdFromMP);
                return;
            }

            Long orderId = Long.parseLong(paymentInfo.getExternalReference());
            String newStatusFromMP = paymentInfo.getStatus().toString().toLowerCase();

            logger.info("[WebhookService] Llamando a OrderService para actualizar pedido #{} al estado: {}", orderId, newStatusFromMP);
            orderService.updateOrderStatusFromWebhook(orderId, newStatusFromMP);

        } catch (NumberFormatException e) {
            logger.error("[WebhookService] Error al parsear el ID del pago o de la orden: {}", data.get("id"), e);
        } catch (MPException e) {
            logger.error("[WebhookService] Error de SDK/API de Mercado Pago al procesar 'payment':", e);
        } catch (Exception e) {
            logger.error("[WebhookService] Error inesperado al procesar 'payment':", e);
        }
    }
}