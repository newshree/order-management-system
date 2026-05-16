package com.ecom.payment.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ecom.payment.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RefundResponse - DTO for refund operation response.
 *
 * Returned when a refund is successfully initiated.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundResponse {

    private UUID refundId;

    private UUID paymentId;

    private BigDecimal refundAmount;

    private PaymentStatus status;

    private String reason;

    private LocalDateTime createdAt;
}
