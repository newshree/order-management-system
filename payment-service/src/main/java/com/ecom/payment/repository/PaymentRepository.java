package com.ecom.payment.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.payment.entity.Payment;
import com.ecom.payment.enums.PaymentStatus;

/**
 * PaymentRepository - Data access interface for Payment entity.
 *
 * Provides database operations for Payment records.
 * Extends JpaRepository for standard CRUD operations.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    /**
     * Find all payments by order ID.
     *
     * @param orderId the order identifier
     * @return list of payments for the order
     */
    List<Payment> findByOrderId(UUID orderId);

    /**
     * Find all payments by user ID.
     *
     * @param userId the user identifier
     * @return list of payments by the user
     */
    List<Payment> findByUserId(UUID userId);

    /**
     * Find the most recent payment for an order.
     *
     * @param orderId the order identifier
     * @return optional containing the most recent payment
     */
    Optional<Payment> findFirstByOrderIdOrderByCreatedAtDesc(UUID orderId);

    /**
     * Find payments by status.
     *
     * @param status the payment status
     * @return list of payments with the given status
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     * Find payment by transaction ID.
     *
     * @param transactionId the transaction identifier
     * @return optional containing the payment
     */
    Optional<Payment> findByTransactionId(String transactionId);
}
