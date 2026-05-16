package com.ecom.payment.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ecom.payment.enums.PaymentStatus;
import com.ecom.payment.enums.PaymentTransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PaymentTransactionResponse - DTO for payment transaction details.
 *
 * Used in audit trail and transaction history endpoints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransactionResponse {

    private UUID transactionId;

    private UUID paymentId;

    private PaymentTransactionType transactionType;

    private PaymentStatus status;

    private BigDecimal amount;

    private LocalDateTime createdAt;
}
