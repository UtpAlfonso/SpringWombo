package com.jugueteria.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    /**
     * Un booleano simple para saber si la transacción fue exitosa.
     */
    private boolean success;

    /**
     * El estado final de la transacción según la pasarela (ej: "approved", "succeeded", "declined", "failed").
     */
    private String status;

    /**
     * El identificador único de la transacción en el sistema de la pasarela de pago (ej: "txn_123abc...", "pi_xyz...").
     * Es crucial para futuras referencias, como reembolsos.
     */
    private String transactionId;

    /**
     * Opcional: Un mensaje de error si la transacción falló.
     */
    private String errorMessage;

    // Constructor conveniente para respuestas sin error
    public PaymentResponse(boolean success, String status, String transactionId) {
        this.success = success;
        this.status = status;
        this.transactionId = transactionId;
    }
}
