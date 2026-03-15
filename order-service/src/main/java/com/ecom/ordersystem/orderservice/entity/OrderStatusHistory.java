package com.ecom.ordersystem.orderservice.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ecom.ordersystem.orderservice.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrderStatusHistory - Audit trail entity tracking all status changes of an order.
 * 
 * Maintains complete history of order status transitions with timestamps and user information.
 * Enables order tracking, compliance auditing, and analytics.
 * 
 * Database Mapping:
 * - Table Name: order_status_history
 * - Primary Key: status_id (UUID)
 * - Foreign Key: order_id (references orders table)
 * 
 * Relationship:
 * - Many-to-One with Order
 * - Child entity in Order aggregate
 * 
 * Design Patterns:
 * - Event Sourcing Pattern: Maintains event history
 * - Audit Trail Pattern: Tracks all changes with who and when
 * - Entity Pattern: JPA entity with ORM mapping
 * 
 * Purpose:
 * - Maintains immutable audit trail
 * - Enables order tracking and monitoring
 * - Provides compliance and forensic data
 * - Supports analytics and reporting
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_status_history")
public class OrderStatusHistory {

    /**
     * Unique identifier for the history record.
     * 
     * - Type: UUID
     * - Generated: Automatically via database
     * - Primary Key: Yes
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "status_id")
    private UUID id;

    /**
     * Reference to the parent Order entity.
     * 
     * Many-to-One relationship: One order can have many status history entries.
     * 
     * - Type: Order entity
     * - Required: Yes
     * - Foreign Key Column: order_id
     * - Cascade: Handled by Order entity
     * 
     * @see Order
     */
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Order status at this point in time.
     * 
     * Represents the status the order transitioned TO at this timestamp.
     * 
     * Valid values: CREATED, PAYMENT_PENDING, PAID, PACKED, SHIPPED, DELIVERED, CANCELLED, FAILED
     * 
     * - Type: OrderStatus enum
     * - Required: Yes
     * - Persistence: Stored as STRING in database
     * - Purpose: Track status progression over time
     * 
     * @see OrderStatus
     */
    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /**
     * User or system entity that triggered the status update.
     * 
     * Identifies who made the change:
     * - "SYSTEM" for automated transitions
     * - User ID or username for manual updates
     * - Service name for inter-service updates
     * 
     * - Type: String
     * - Length: Max 100 characters
     * - Required: Yes
     * - Example: "SYSTEM", "admin@company.com", "payment-service"
     */
    @Column(name = "updated_by", nullable = false, length = 100)
    private String updatedBy;

    /**
     * Timestamp when this status change occurred.
     * 
     * Records the exact moment of the status transition.
     * Used for time-series analysis and audit trails.
     * 
     * - Type: LocalDateTime
     * - Required: Yes
     * - Format: ISO-8601 (YYYY-MM-DD HH:mm:ss)
     * - Immutable: Set at creation, never updated
     * - Uniqueness: Multiple entries can have same timestamp
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

