package com.jugueteria.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;
    /**
     * Identificador único de la transacción generado por la pasarela de pagos externa (ej: "pi_3L..." de Stripe).
     * Es crucial para rastrear el pago en el dashboard del proveedor.
     */
    @Column(name = "id_transaccion_externa", unique = true)
    private String idTransaccionExterna;
    /**
     * Estado del pago según la pasarela (ej: "succeeded", "pending", "failed", "approved", "rejected").
     */
    @Column(nullable = false, length = 50)
    private String estado;
    /**
     * Monto que fue procesado. Se guarda una copia por si hay discrepancias o para auditoría.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
    /**
     * Método de pago utilizado, según lo informa la pasarela (ej: "card", "visa", "yape", "paypal").
     */
    @Column(length = 50)
    private String metodoPago;
    /**
     * Moneda en la que se realizó el pago (ej: "PEN", "USD").
     */
    @Column(length = 10)
    private String moneda;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @PrePersist
    protected void onCreate() {
        fechaPago = LocalDateTime.now();
    }
}
