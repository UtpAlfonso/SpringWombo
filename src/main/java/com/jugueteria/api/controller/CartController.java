package com.jugueteria.api.controller;

import com.jugueteria.api.dto.request.CartItemRequest;
import com.jugueteria.api.dto.request.CartItemUpdateRequest;
import com.jugueteria.api.dto.response.CartResponse;
import com.jugueteria.api.entity.Usuario;
import com.jugueteria.api.services.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getMyCart(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(cartService.getCartByUsuario(usuario));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToMyCart(@AuthenticationPrincipal Usuario usuario, @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(usuario, request));
    }

    @DeleteMapping("/items/{productoId}")
    public ResponseEntity<CartResponse> removeItemFromMyCart(@AuthenticationPrincipal Usuario usuario, @PathVariable Long productoId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(usuario, productoId));
    }

     @PutMapping("/items/{productoId}")
    public ResponseEntity<CartResponse> updateItemInMyCart(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long productoId,
            @Valid @RequestBody CartItemUpdateRequest request
    ) {
        return ResponseEntity.ok(cartService.updateItemQuantity(usuario, productoId, request.getCantidad()));
    }
    
    @DeleteMapping
    public ResponseEntity<Void> clearMyCart(@AuthenticationPrincipal Usuario usuario) {
        cartService.clearCart(usuario);
        return ResponseEntity.noContent().build();
    }
}