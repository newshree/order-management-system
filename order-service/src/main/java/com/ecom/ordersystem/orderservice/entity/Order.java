package com.ecom.ordersystem.orderservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.ecom.ordersystem.orderservice.enums.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order - Domain model representing a customer order.
 * 
 * Core entity for the Order Management System.
 * Maintains relationships with order items, status history, and shipping address.
 * 
 * Database Mapping:
 * - Table Name: orders
 * - Primary Key: order_id (UUID)
 * - Unique Constraint: order_number
 * 
 * Relationships:
 * - One-to-Many with OrderItem (cascade delete)
 * - One-to-Many with OrderStatusHistory (cascade delete)
 * - One-to-One with OrderAddress (cascade delete)
 * 
 * Design Patterns:
 * - Entity Pattern: JPA entity with ORM mapping
 * - Aggregate Root Pattern: Order is root of order aggregate
 * - Value Object: OrderStatus enum for type-safe status
 * 
 * Lombok Annotations:
 * - @Data: Generates getter, setter, toString, equals, hashCode
 * - @NoArgsConstructor: Generates default constructor for JPA
 * - @AllArgsConstructor: Generates constructor with all fields
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    /**
     * Unique identifier for the order.
     * 
     * - Type: UUID
     * - Generated: Automatically via database
     * - Immutable: Cannot be changed after creation
     * - Primary Key: Yes
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    private UUID id;

    /**
     * Human-readable order number for customer-facing operations.
     * 
     * Format: ORD-{YEAR}-{SEQUENCE}
     * Example: ORD-2026-000001
     * 
     * - Type: String
     * - Length: Max 50 characters
     * - Uniqueness: Globally unique
     * - Immutable: Set at creation
     */
    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    /**
     * Tenant identifier for multi-tenant system support.
     * 
     * Enables data isolation between different customers/organizations
     * in a multi-tenant architecture.
     * 
     * - Type: UUID
     * - Required: Yes
     * - Purpose: Data segregation and isolation
     */
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    /**
     * Customer/User identifier who placed the order.
     * 
     * Reference to the user/customer entity in user service.
     * Used for user-specific order queries and tracking.
     * 
     * - Type: UUID
     * - Required: Yes
     * - Foreign Reference: User service
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * Current status of the order in the order lifecycle.
     * 
     * Valid statuses: CREATED, PAYMENT_PENDING, PAID, PACKED, SHIPPED, DELIVERED, CANCELLED, FAILED
     * 
     * - Type: OrderStatus enum
     * - Required: Yes
     * - Persistence: Stored as STRING in database
     * - Initial Value: CREATED
     */
    @Column(name = "order_status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /**
     * Total order amount (final price).
     * 
     * Represents the complete cost of the order including all items.
     * 
     * - Type: BigDecimal
     * - Precision: 19 digits total
     * - Scale: 2 decimal places (cents)
     * - Example: 1234.56 (stores as DECIMAL(19,2))
     * - Required: Yes
     */
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Timestamp when the order was created.
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
     * Timestamp of the last update to the order.
     * 
     * Updated whenever any field is modified:
     * - Status changes
     * - Shipping address updates
     * 
     * - Type: LocalDateTime
     * - Auto-updated: Yes
     * - Format: ISO-8601 (YYYY-MM-DD HH:mm:ss)
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Collection of items included in this order.
     * 
     * Relationship:
     * - Type: One-to-Many
     * - Cascade: ALL (delete items when order is deleted)
     * - Orphan Removal: Enabled (delete items when removed from order)
     * - Lazy Loading: True (loaded on demand)
     * 
     * @see OrderItem
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    /**
     * Audit trail of all status changes for this order.
     * 
     * Maintains complete history of status transitions with timestamps
     * and user information for compliance and traceability.
     * 
     * Relationship:
     * - Type: One-to-Many
     * - Cascade: ALL (delete history when order is deleted)
     * - Orphan Removal: Enabled (delete history when removed from order)
     * - Lazy Loading: True (loaded on demand)
     * 
     * @see OrderStatusHistory
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderStatusHistory> statusHistory;

    /**
     * Shipping/Delivery address for this order.
     * 
     * Contains receiver name, contact, and full delivery address.
     * 
     * Relationship:
     * - Type: One-to-One
     * - Cascade: ALL (delete address when order is deleted)
     * - Orphan Removal: Enabled (delete address when removed from order)
     * - Lazy Loading: True (loaded on demand)
     * - Unique: One address per order
     * 
     * @see OrderAddress
     */
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private OrderAddress orderAddress;
}
