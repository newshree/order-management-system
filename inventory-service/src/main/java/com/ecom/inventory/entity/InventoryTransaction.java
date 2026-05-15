package com.ecom.inventory.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.ecom.inventory.enums.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Audit log for all inventory transactions.
 * Tracks quantity changes with transaction type and related order ID.
 */
@Entity
@Table(
    name = "inventory_transactions"
    // indexes = {
    //     @Index(name = "idx_order_id", columnList = "order_id"),
    //     @Index(name = "idx_inventory_id", columnList = "inventory_id"),
    //     @Index(name = "idx_product_id", columnList = "product_id"),
    //     @Index(name = "idx_tenant_id", columnList = "tenant_id"),
    //     @Index(name = "idx_transaction_type", columnList = "transaction_type"),
    //     @Index(name = "idx_created_at", columnList = "created_at")
    // }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransaction {

    /**
     * Unique identifier for the transaction.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Tenant ID for multi-tenancy support.
     */
    @NotNull
    @Column(nullable = false)
    private String tenantId;

    /**
     * Product ID associated with the transaction.
     */
    @NotNull
    @Column(nullable = false)
    private String productId;

    /**
     * Order ID associated with the transaction (if applicable).
     */
    @Column
    private String orderId;

    /**
     * Reference to the inventory record.
     */
    @Column
    private UUID inventoryId;

    /**
     * Quantity changed in this transaction.
     */
    @NotNull
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Type of transaction: RESERVE, COMMIT, RELEASE, CREATE, UPDATE, DELETE.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    /**
     * Timestamp when the transaction occurred.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

}

