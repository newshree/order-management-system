package com.ecom.payment.mapper;

import org.springframework.stereotype.Component;

import com.ecom.payment.dto.response.PaymentResponse;
import com.ecom.payment.dto.response.PaymentTransactionResponse;
import com.ecom.payment.entity.Payment;
import com.ecom.payment.entity.PaymentTransaction;

/**
 * PaymentMapper - Maps between Payment entities and DTOs.
 *
 * Provides conversion methods for:
 * - Payment → PaymentResponse
 * - PaymentTransaction → PaymentTransactionResponse
 *
 * Design Pattern:
 * - Mapper Pattern: Separation of entity and DTO layers
 */
@Component
public class PaymentMapper {

    /**
     * Convert Payment entity to PaymentResponse DTO.
     *
     * @param payment the Payment entity
     * @return PaymentResponse DTO
     */
    public PaymentResponse paymentToPaymentResponse(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

    /**
     * Convert PaymentTransaction entity to PaymentTransactionResponse DTO.
     *
     * @param transaction the PaymentTransaction entity
     * @return PaymentTransactionResponse DTO
     */
    public PaymentTransactionResponse paymentTransactionToResponse(PaymentTransaction transaction) {
        if (transaction == null) {
            return null;
        }

        return PaymentTransactionResponse.builder()
                .transactionId(transaction.getId())
                .paymentId(transaction.getPayment().getId())
                .transactionType(transaction.getTransactionType())
                .status(transaction.getStatus())
                .amount(transaction.getAmount())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
