package com.jugueteria.api.controller; // <-- ¿Paquete correcto?

import com.jugueteria.api.entity.Usuario;
import com.jugueteria.api.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController; // <-- ¿Import correcto?

import java.util.Map;

@RestController // <-- ¿ESTÁ ESTA ANOTACIÓN?
@RequestMapping("/api/v1/payments") // <-- ¿ESTÁ ESTA ANOTACIÓN?
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-preference") // <-- ¿ESTÁ ESTA ANOTACIÓN?
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Map<String, String>> createPreference(@AuthenticationPrincipal Usuario usuario) {
        String preferenceId = paymentService.createPaymentPreference(usuario);
        return ResponseEntity.ok(Map.of("preferenceId", preferenceId));
    }
}