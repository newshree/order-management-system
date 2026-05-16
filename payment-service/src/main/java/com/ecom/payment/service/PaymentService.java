package com.ecom.payment.service;

import java.util.List;
import java.util.UUID;

import com.ecom.payment.dto.request.InitiatePaymentRequest;
import com.ecom.payment.dto.request.RefundPaymentRequest;
import com.ecom.payment.dto.response.PaymentResponse;
import com.ecom.payment.dto.response.PaymentTransactionResponse;
import com.ecom.payment.dto.response.RefundResponse;

/**
 * PaymentService - Business logic interface for payment operations.
 *
 * Defines all business operations for payment management including:
 * - Payment initiation and processing
 * - Payment status management
 * - Refund handling
 * - Transaction history
 */
public interface PaymentService {

    /**
     * Initiates a payment for an order.
     *
     * Business Logic:
     * 1. Validate order exists (external call to Order Service)
     * 2. Validate payment amount is positive
     * 3. Create payment record with PENDING status
     * 4. Generate transaction ID (TXN + timestamp)
     * 5. Simulate payment processing (random success/failure)
     * 6. Update payment status (SUCCESS or FAILED)
     * 7. Create payment transaction record for audit trail
     * 8. Return payment details
     *
     * @param request InitiatePaymentRequest containing order and payment details
     * @return PaymentResponse with payment confirmation
     * @throws ResourceNotFoundException if order not found
     * @throws BadRequestException if amount is invalid
     */
    PaymentResponse initiatePayment(InitiatePaymentRequest request);

    /**
     * Retrieves payment details by payment ID.
     *
     * @param paymentId the payment identifier
     * @return PaymentResponse with payment details
     * @throws ResourceNotFoundException if payment not found
     */
    PaymentResponse getPaymentById(UUID paymentId);

    /**
     * Retrieves all payments for a specific order.
     *
     * @param orderId the order identifier
     * @return list of PaymentResponse for the order
     * @throws ResourceNotFoundException if no payments found
     */
    List<PaymentResponse> getPaymentsByOrderId(UUID orderId);

    /**
     * Retrieves all payments for a specific user.
     *
     * @param userId the user identifier
     * @return list of PaymentResponse for the user
     * @throws ResourceNotFoundException if no payments found
     */
    List<PaymentResponse> getPaymentsByUserId(UUID userId);

    /**
     * Processes refund for a payment.
     *
     * Business Logic:
     * 1. Validate payment exists
     * 2. Validate payment status is SUCCESS
     * 3. Validate payment not already refunded
     * 4. Create refund transaction
     * 5. Update payment status to REFUNDED
     * 6. Record refund in payment transactions table
     * 7. Return refund details
     *
     * @param request RefundPaymentRequest containing payment and reason
     * @return RefundResponse with refund confirmation
     * @throws ResourceNotFoundException if payment not found
     * @throws BadRequestException if payment is not eligible for refund
     */
    RefundResponse refundPayment(RefundPaymentRequest request);

    /**
     * Retrieves transaction history for a payment.
     *
     * @param paymentId the payment identifier
     * @return list of PaymentTransactionResponse for the payment
     * @throws ResourceNotFoundException if payment not found
     */
    List<PaymentTransactionResponse> getPaymentTransactionHistory(UUID paymentId);

    /**
     * Health check endpoint for service monitoring.
     *
     * @return true if service is healthy
     */
    boolean healthCheck();
}
