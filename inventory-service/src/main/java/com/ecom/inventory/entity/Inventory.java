package com.ecom.inventory.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the inventory for a product.
 * Tracks available and reserved stock quantities per product.
 */
@Entity
@Table(
    name = "inventories",
    indexes = {
        @Index(name = "idx_tenant_product", columnList = "product_id, tenant_id"),
        @Index(name = "idx_tenant_product_reserved", columnList = "product_id, tenant_id, reserved_quantity"),
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_tenant_product", columnNames = {"tenant_id", "product_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    /**
     * Unique identifier for the inventory record.
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
     * Product ID associated with this inventory.
     */
    @NotNull
    @Column(nullable = false)
    private String productId;

    /**
     * Available quantity that can be ordered.
     * Available = Total - Reserved
     */
    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer availableQuantity;

    /**
     * Reserved quantity pending payment.
     * Increases when order reserves stock.
     */
    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer reservedQuantity;

    /**
     * Timestamp when the record was created.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the record was last updated.
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

