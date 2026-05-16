package com.ecom.payment.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.payment.entity.PaymentTransaction;
import com.ecom.payment.enums.PaymentTransactionType;

/**
 * PaymentTransactionRepository - Data access interface for PaymentTransaction entity.
 *
 * Provides database operations for PaymentTransaction records.
 * Extends JpaRepository for standard CRUD operations.
 */
@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {

    /**
     * Find all transactions for a specific payment.
     *
     * @param paymentId the payment identifier
     * @return list of transactions for the payment
     */
    List<PaymentTransaction> findByPaymentId(UUID paymentId);

    /**
     * Find transactions by type (PAYMENT or REFUND).
     *
     * @param transactionType the transaction type
     * @return list of transactions of the given type
     */
    List<PaymentTransaction> findByTransactionType(PaymentTransactionType transactionType);

    /**
     * Find all transactions for a payment by type.
     *
     * @param paymentId the payment identifier
     * @param transactionType the transaction type
     * @return list of matching transactions
     */
    List<PaymentTransaction> findByPaymentIdAndTransactionType(UUID paymentId, PaymentTransactionType transactionType);
}
