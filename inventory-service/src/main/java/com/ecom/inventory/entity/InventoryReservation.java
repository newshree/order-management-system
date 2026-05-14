package com.ecom.inventory.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ecom.inventory.enums.ReservationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a stock reservation for an order.
 * Tracks reservations with status: RESERVED, COMMITTED, or RELEASED.
 */
@Entity
@Table(
    name = "inventory_reservations",
    // indexes = {
    //     @Index(name = "idx_order_id", columnList = "order_id"),
    //     @Index(name = "idx_inventory_id", columnList = "inventory_id"),
    //     @Index(name = "idx_product_id", columnList = "product_id"),
    //     @Index(name = "idx_tenant_id", columnList = "tenant_id"),
    //     @Index(name = "idx_status", columnList = "status")
    // },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_order_inventory_product",
            columnNames = {"order_id", "inventory_id", "product_id"}
        )
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReservation {

    /**
     * Unique identifier for the reservation.
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
     * Order ID associated with this reservation.
     */
    @NotNull
    @Column(nullable = false)
    private String orderId;

    /**
     * Reference to the inventory record.
     */
    @NotNull
    @Column(nullable = false)
    private UUID inventoryId;

    /**
     * Product ID for reference.
     */
    @NotNull
    @Column(nullable = false)
    private String productId;

    /**
     * Quantity reserved for the order.
     */
    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Status of the reservation: RESERVED, COMMITTED, or RELEASED.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    /**
     * Timestamp when the reservation was created.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the reservation was last updated.
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}

