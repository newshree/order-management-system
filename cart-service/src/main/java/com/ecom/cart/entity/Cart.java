package com.ecom.cart.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.ecom.cart.enums.CartStatus;

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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cart - Domain model representing a shopping cart for a user.
 * 
 * Core entity for the Cart Service.
 * Maintains relationship with cart items.
 * 
 * Database Mapping:
 * - Table Name: carts
 * - Primary Key: cart_id (UUID)
 * - Unique Constraint: user_id (one cart per user)
 * 
 * Relationships:
 * - One-to-Many with CartItem (cascade delete)
 * 
 * Design Patterns:
 * - Entity Pattern: JPA entity with ORM mapping
 * - Aggregate Root Pattern: Cart is root of cart aggregate
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
@Table(
    name = "carts",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_id", name = "uq_carts_user_id")
    }
)
public class Cart {

    /**
     * Unique identifier for the cart.
     * 
     * - Type: UUID
     * - Generated: Automatically via database
     * - Immutable: Cannot be changed after creation
     * - Primary Key: Yes
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cart_id")
    private UUID id;

    /**
     * User identifier who owns this cart.
     * 
     * - Type: UUID
     * - Required: Yes
     * - Unique: Yes (one cart per user)
     * - Immutable: Set at creation
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    /**
     * Current status of the cart.
     * 
     * Valid statuses: ACTIVE, ABANDONED, CONVERTED, EXPIRED
     * 
     * - Type: CartStatus enum
     * - Required: Yes
     * - Persistence: Stored as STRING in database
     * - Initial Value: ACTIVE
     */
    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CartStatus status = CartStatus.ACTIVE;

    /**
     * Total cart amount (sum of all items).
     * 
     * Represents the current total cost of all items in the cart.
     * 
     * - Type: BigDecimal
     * - Precision: 19 digits total
     * - Scale: 2 decimal places (cents)
     * - Example: 1234.56 (stored as DECIMAL(19,2))
     * - Default: 0.00
     */
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /**
     * Timestamp when the cart was created.
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
     * Timestamp of the last update to the cart.
     * 
     * Updated whenever:
     * - Items are added/removed/updated
     * - Quantity changes
     * - Status changes
     * 
     * - Type: LocalDateTime
     * - Auto-updated: Yes
     * - Format: ISO-8601 (YYYY-MM-DD HH:mm:ss)
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Collection of items in this cart.
     * 
     * Relationship:
     * - Type: One-to-Many
     * - Cascade: ALL (delete items when cart is deleted)
     * - Orphan Removal: Enabled (delete items when removed from cart)
     * - Lazy Loading: True (loaded on demand)
     * 
     * @see CartItem
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;

    /**
     * Lifecycle method: Set createdAt and updatedAt before persisting.
     */
    @jakarta.persistence.PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = CartStatus.ACTIVE;
        }
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }
    }

    /**
     * Lifecycle method: Update updatedAt before updating.
     */
    @jakarta.persistence.PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
