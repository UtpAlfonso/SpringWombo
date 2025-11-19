package com.jugueteria.api.services.impl;

import com.jugueteria.api.entity.Pedido;
import com.jugueteria.api.entity.Usuario;
import com.jugueteria.api.services.OrderService;
import com.jugueteria.api.services.PaymentService;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException; // <-- Importación necesaria
import com.mercadopago.exceptions.MPException;     // <-- Importación necesaria
import com.mercadopago.resources.preference.Preference;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderService orderService;

    @Value("${mercadopago.access-token}")
    private String mpAccessToken;
    
    @Value("${app.webhook-url}")
    private String webhookUrl;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(mpAccessToken);
    }

    @Override
    public String createPaymentPreference(Usuario usuario) {
        // --- ENVOLVEMOS TODA LA LÓGICA EN UN TRY...CATCH ---
        try {
            Pedido pedido = orderService.createPendingOrder(usuario);

            List<PreferenceItemRequest> items = new ArrayList<>();
            pedido.getDetalles().forEach(detalle ->
                items.add(PreferenceItemRequest.builder()
                        .id(detalle.getProducto().getSku())
                        .title(detalle.getProducto().getNombre())
                        .quantity(detalle.getCantidad())
                        .unitPrice(detalle.getPrecioUnitario())
                        .currencyId("PEN")
                        .build())
            );
            
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("http://localhost:4200/payment-success")
                    .failure("http://localhost:4200/payment-failure")
                    .pending("http://localhost:4200/payment-pending")
                    .build();
            
            PreferenceClient client = new PreferenceClient();
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .externalReference(pedido.getId().toString())
                    .backUrls(backUrls)
                    .notificationUrl(webhookUrl + "/api/v1/webhooks/mercadopago")
                    .build();

            // Esta es la línea que puede lanzar la excepción
            Preference preference = client.create(preferenceRequest);
            
            return preference.getId();
            
        } catch (MPApiException e) {
            // Error específico de la API (ej. datos malformados, token inválido)
            System.err.println("Error de API de Mercado Pago al crear preferencia: " + e.getApiResponse().getContent());
            throw new RuntimeException("Error al comunicarse con la pasarela de pagos (API).");
        } catch (MPException e) {
           System.err.println("--- ERROR CATASTRÓFICO AL CREAR LA PREFERENCIA DE MP ---");
        // Imprime el stack trace completo en la consola
        e.printStackTrace(); 
        throw new RuntimeException("No se pudo generar el link de pago.", e);
        }
    }
}