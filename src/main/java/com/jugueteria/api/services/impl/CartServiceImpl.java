package com.jugueteria.api.services.impl;
import com.jugueteria.api.dto.request.CartItemRequest;
import com.jugueteria.api.dto.response.CartItemResponse;
import com.jugueteria.api.dto.response.CartResponse;
import com.jugueteria.api.entity.CarritoItem;
import com.jugueteria.api.entity.Producto;
import com.jugueteria.api.entity.Usuario;
import com.jugueteria.api.exception.ResourceNotFoundException;
import com.jugueteria.api.repository.CarritoItemRepository;
import com.jugueteria.api.repository.ProductoRepository;
import com.jugueteria.api.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
private final CarritoItemRepository carritoItemRepository;
private final ProductoRepository productoRepository;

@Override
public CartResponse getCartByUsuario(Usuario usuario) {
    List<CarritoItem> items = carritoItemRepository.findByUsuario(usuario);
    
    List<CartItemResponse> itemResponses = items.stream()
            .map(this::convertToItemResponse)
            .collect(Collectors.toList());
    
    BigDecimal total = itemResponses.stream()
            .map(CartItemResponse::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    CartResponse cartResponse = new CartResponse();
    cartResponse.setItems(itemResponses);
    cartResponse.setTotal(total);
    return cartResponse;
}

@Override
@Transactional
public CartResponse addItemToCart(Usuario usuario, CartItemRequest request) {
    Producto producto = productoRepository.findById(request.getProductoId())
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado."));

    CarritoItem item = carritoItemRepository.findByUsuarioAndProductoId(usuario, request.getProductoId())
            .orElse(new CarritoItem());
    
    int newQuantity = item.getId() == null ? request.getCantidad() : item.getCantidad() + request.getCantidad();
    
    if (producto.getStock() < newQuantity) {
        throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getNombre());
    }
    
    item.setUsuario(usuario);
    item.setProducto(producto);
    item.setCantidad(newQuantity);
    
    carritoItemRepository.save(item);
    return getCartByUsuario(usuario);
}

@Override
@Transactional
public CartResponse removeItemFromCart(Usuario usuario, Long productoId) {
    CarritoItem item = carritoItemRepository.findByUsuarioAndProductoId(usuario, productoId)
            .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado en el carrito."));
    
    carritoItemRepository.delete(item);
    return getCartByUsuario(usuario);
}

@Override
@Transactional
public void clearCart(Usuario usuario) {
    carritoItemRepository.deleteByUsuario(usuario);
}


    private CartItemResponse convertToItemResponse(CarritoItem item) {
        CartItemResponse response = new CartItemResponse();
        Producto producto = item.getProducto();

        response.setProductoId(producto.getId());
        response.setProductoNombre(producto.getNombre());
        response.setPrecioUnitario(producto.getPrecio());
        response.setCantidad(item.getCantidad());
        response.setSubtotal(producto.getPrecio().multiply(new BigDecimal(item.getCantidad())));
        response.setProductoImageUrl(producto.getImageUrl());
        response.setProductoStock(producto.getStock());

        return response;
    }
    
@Override
    @Transactional
    public CartResponse updateItemQuantity(Usuario usuario, Long productoId, int cantidad) {
        CarritoItem item = carritoItemRepository.findByUsuarioAndProductoId(usuario, productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado en el carrito."));

        Producto producto = item.getProducto();
        if (cantidad > producto.getStock()) {
            throw new IllegalStateException("Stock insuficiente. Solo quedan " + producto.getStock() + " unidades.");
        }

        item.setCantidad(cantidad);
        carritoItemRepository.save(item);
        
        return getCartByUsuario(usuario);
    }
}