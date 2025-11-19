package com.jugueteria.api.controller;

import com.jugueteria.api.dto.request.OrderRequest;
import com.jugueteria.api.dto.request.PosOrderRequest;
import com.jugueteria.api.dto.request.ReturnRequest;
import com.jugueteria.api.dto.response.OrderResponse;
import com.jugueteria.api.entity.Usuario;
import com.jugueteria.api.services.OrderService;
// import com.jugueteria.api.service.PdfGenerationService; // Descomentar al implementar
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    // private final PdfGenerationService pdfGenerationService; // Descomentar al implementar

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(orderService.findByUsuario(usuario));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(orderService.findByIdForUser(id, usuario));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id, @RequestBody String status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }
    
    @PostMapping("/returns")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public ResponseEntity<OrderResponse> processReturn(@Valid @RequestBody ReturnRequest request) {
        return ResponseEntity.ok(orderService.processReturn(request));
    }

    @GetMapping("/{id}/invoice")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        byte[] pdfBytes = orderService.generateInvoice(id, usuario);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "boleta-" + id + ".pdf");
        
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @PostMapping("/physical-sale")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public ResponseEntity<OrderResponse> createPhysicalSale(
            @Valid @RequestBody PosOrderRequest request,
            @AuthenticationPrincipal Usuario worker
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createPhysicalSale(request, worker));
    }
}