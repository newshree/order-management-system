package com.ecom.payment.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RefundPaymentRequest - DTO for requesting payment refund.
 *
 * Used by clients to request refund for a payment.
 *
 * Validation:
 * - PaymentId is required
 * - Reason is required
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundPaymentRequest {

    @NotNull(message = "Payment ID is required")
    private UUID paymentId;

    @NotNull(message = "Reason is required")
    private String reason;
}
