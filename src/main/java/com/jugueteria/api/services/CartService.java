package com.jugueteria.api.services;
import com.jugueteria.api.dto.request.CartItemRequest;
import com.jugueteria.api.dto.response.CartResponse;
import com.jugueteria.api.entity.Usuario;
public interface CartService {
    CartResponse getCartByUsuario(Usuario usuario);
    CartResponse addItemToCart(Usuario usuario, CartItemRequest request);
    CartResponse removeItemFromCart(Usuario usuario, Long productoId);
    void clearCart(Usuario usuario);
    CartResponse updateItemQuantity(Usuario usuario, Long productoId, int cantidad);
}