package com.jugueteria.api.services;

import com.jugueteria.api.dto.request.PosOrderRequest;
import com.jugueteria.api.dto.request.ReturnRequest;
import com.jugueteria.api.dto.response.OrderResponse;
import com.jugueteria.api.entity.Pedido;
import com.jugueteria.api.entity.Usuario;
import java.util.List;

public interface OrderService {

 Pedido createPendingOrder(Usuario usuario);
    void updateOrderStatusFromWebhook(Long orderId, String status);

    // --- MÉTODOS DE LECTURA (READ) ---
    List<OrderResponse> findAll();
    List<OrderResponse> findByUsuario(Usuario usuario);
    OrderResponse findByIdForUser(Long orderId, Usuario usuario);

    // --- MÉTODOS DE GESTIÓN (ADMIN/WORKER) ---
    OrderResponse updateStatus(Long orderId, String status);
    byte[] generateInvoice(Long orderId, Usuario usuario);
    OrderResponse createPhysicalSale(PosOrderRequest request, Usuario worker);
    OrderResponse processReturn(ReturnRequest request);
}