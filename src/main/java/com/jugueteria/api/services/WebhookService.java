package com.jugueteria.api.services;
import java.util.Map;
public interface WebhookService {
void processMercadoPagoNotification(Map<String, Object> notification);
}