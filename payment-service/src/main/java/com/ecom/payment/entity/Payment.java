package com.ecom.payment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.ecom.payment.enums.PaymentMethod;
import com.ecom.payment.enums.PaymentStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payment - Domain model representing a payment for an order.
 *
 * Core entity for the Payment Service.
 * Maintains relationship with payment transactions for audit trail.
 *
 * Database Mapping:
 * - Table Name: payments
 * - Primary Key: payment_id (UUID)
 *
 * Relationships:
 * - One-to-Many with PaymentTransaction (cascade delete)
 *
 * Design Patterns:
 * - Entity Pattern: JPA entity with ORM mapping
 * - Aggregate Root Pattern: Payment is root of payment aggregate
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
@Table(name = "payments")
public class Payment {

    /**
     * Unique identifier for the payment.
     *
     * - Type: UUID
     * - Generated: Automatically via database
     * - Immutable: Cannot be changed after creation
     * - Primary Key: Yes
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    private UUID id;

    /**
     * Associated order identifier.
     *
     * - Type: UUID
     * - Required: Yes
     * - References: Order Service (external)
     */
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    /**
     * User who initiated the payment.
     *
     * - Type: UUID
     * - Required: Yes
     * - References: User Service (external)
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * Payment amount.
     *
     * - Type: BigDecimal
     * - Precision: 19 digits total
     * - Scale: 2 decimal places (cents)
     * - Example: 1500.00
     * - Required: Yes
     */
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    /**
     * Selected payment method.
     *
     * Valid values: CARD, UPI, NET_BANKING, COD, WALLET
     * - Type: PaymentMethod enum
     * - Required: Yes
     * - Persistence: Stored as STRING in database
     */
    @Column(name = "payment_method", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    /**
     * Current status of the payment.
     *
     * Valid statuses: PENDING, SUCCESS, FAILED, REFUNDED
     * - Type: PaymentStatus enum
     * - Required: Yes
     * - Persistence: Stored as STRING in database
     * - Initial Value: PENDING
     */
    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    /**
     * Transaction identifier for external payment gateway.
     *
     * Format: TXN12345 (simulation for Phase-1)
     * - Type: String
     * - Length: 255 characters
     * - Unique: Within the service
     * - Generated: By payment processor
     */
    @Column(name = "transaction_id", length = 255)
    private String transactionId;

    /**
     * Timestamp when the payment was created.
     *
     * - Type: LocalDateTime
     * - Auto-set: Yes (at creation)
     * - Updatable: No (immutable after creation)
     * - Required: Yes
     * - Format: ISO-8601 (YYYY-MM-DD HH:mm:ss)
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of the last update to the payment.
     *
     * Updated whenever:
     * - Status changes (PENDING → SUCCESS, FAILED, REFUNDED)
     * - Payment is refunded
     *
     * - Type: LocalDateTime
     * - Auto-updated: Yes
     * - Format: ISO-8601 (YYYY-MM-DD HH:mm:ss)
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Collection of transactions related to this payment.
     *
     * Relationship:
     * - Type: One-to-Many
     * - Cascade: ALL (delete transactions when payment is deleted)
     * - Orphan Removal: Enabled
     * - Lazy Loading: True (loaded on demand)
     *
     * Includes: Payment transaction and any refund transactions
     *
     * @see PaymentTransaction
     */
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentTransaction> transactions;

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = PaymentStatus.PENDING;
        }
    }

    @jakarta.persistence.PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
