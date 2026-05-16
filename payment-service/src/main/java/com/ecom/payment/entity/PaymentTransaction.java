package com.ecom.payment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ecom.payment.enums.PaymentStatus;
import com.ecom.payment.enums.PaymentTransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PaymentTransaction - Audit trail for all payment events.
 *
 * Tracks every payment-related transaction:
 * - Initial payment attempts
 * - Payment successes/failures
 * - Refund transactions
 *
 * Database Mapping:
 * - Table Name: payment_transactions
 * - Primary Key: transaction_id (UUID)
 * - Foreign Key: payment_id (references payments table)
 *
 * Relationships:
 * - Many-to-One with Payment
 *
 * Design Patterns:
 * - Event Sourcing: Maintains complete history of all payment events
 * - Audit Trail: Immutable record of all changes
 *
 * Lombok Annotations:
 * - @Data: Generates getter, setter, toString, equals, hashCode
 * - @NoArgsConstructor: Generates default constructor for JPA
 * - @AllArgsConstructor: Generates constructor with all fields
 * - @Builder: Builder pattern for entity construction
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payment_transactions")
public class PaymentTransaction {

    /**
     * Unique identifier for the transaction.
     *
     * - Type: UUID
     * - Generated: Automatically via database
     * - Immutable: Cannot be changed after creation
     * - Primary Key: Yes
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id")
    private UUID id;

    /**
     * Reference to the parent payment.
     *
     * - Type: UUID (Foreign Key)
     * - Required: Yes
     * - Relationship: Many-to-One
     * - Cascade: Inherited from Payment
     *
     * @see Payment
     */
    @ManyToOne
    @JoinColumn(
            name = "payment_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_payment_transactions_payment_id")
    )
    private Payment payment;

    /**
     * Type of transaction.
     *
     * Valid types: PAYMENT, REFUND
     * - Type: PaymentTransactionType enum
     * - Required: Yes
     * - Persistence: Stored as STRING in database
     */
    @Column(name = "transaction_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private PaymentTransactionType transactionType;

    /**
     * Status of this transaction.
     *
     * Valid statuses: PENDING, SUCCESS, FAILED, REFUNDED
     * - Type: PaymentStatus enum
     * - Required: Yes
     * - Persistence: Stored as STRING in database
     */
    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    /**
     * Transaction amount.
     *
     * - Type: BigDecimal
     * - Precision: 19 digits total
     * - Scale: 2 decimal places
     * - Required: Yes
     * - Can be different from original payment (e.g., partial refund)
     */
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    /**
     * Timestamp when the transaction occurred.
     *
     * - Type: LocalDateTime
     * - Auto-set: Yes (at creation)
     * - Updatable: No (immutable)
     * - Required: Yes
     * - Format: ISO-8601 (YYYY-MM-DD HH:mm:ss)
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
