package com.ecom.payment.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ecom.payment.enums.PaymentMethod;
import com.ecom.payment.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PaymentResponse - DTO for payment details response.
 *
 * Returned by all payment-related endpoints.
 * Contains all relevant payment information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private UUID paymentId;

    private UUID orderId;

    private UUID userId;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus status;

    private String transactionId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
