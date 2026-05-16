package com.ecom.payment.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import com.ecom.payment.enums.PaymentMethod;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * InitiatePaymentRequest - DTO for initiating a payment.
 *
 * Used by clients to request payment processing for an order.
 *
 * Validation:
 * - All fields are required
 * - Amount must be greater than 0
 * - PaymentMethod must be valid enum value
 * - UUIDs must be valid format
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitiatePaymentRequest {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}
