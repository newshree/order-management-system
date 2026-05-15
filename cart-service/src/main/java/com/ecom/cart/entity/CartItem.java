package com.ecom.cart.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
import lombok.ToString;

/**
 * CartItem - Domain model representing an individual item in a shopping cart.
 * 
 * Represents a product added to a cart with quantity and pricing information.
 * 
 * Database Mapping:
 * - Table Name: cart_items
 * - Primary Key: item_id (UUID)
 * 
 * Relationships:
 * - Many-to-One with Cart (foreign key: cart_id)
 * 
 * Design Patterns:
 * - Entity Pattern: JPA entity with ORM mapping
 * - Value Object: Represents a single cart item
 * 
 * Lombok Annotations:
 * - @Data: Generates getter, setter, toString, equals, hashCode
 * - @NoArgsConstructor: Generates default constructor for JPA
 * - @AllArgsConstructor: Generates constructor with all fields
 * - @Builder: Builder pattern for entity construction
 * - @ToString.Exclude: Excludes cart from toString to prevent circular references
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cart_items")
public class CartItem {

    /**
     * Unique identifier for the cart item.
     * 
     * - Type: UUID
     * - Generated: Automatically via database
     * - Immutable: Cannot be changed after creation
     * - Primary Key: Yes
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "item_id")
    private UUID id;

    /**
     * Product identifier (reference to Product Service).
     * 
     * - Type: UUID
     * - Required: Yes
     * - Purpose: Link to external product service
     * - Not a database foreign key (external reference)
     */
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    /**
     * Product name (snapshot from time of adding to cart).
     * 
     * Stored as snapshot because product details may change later.
     * 
     * - Type: String
     * - Max Length: 255
     * - Required: Yes
     */
    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    /**
     * Quantity of this item in the cart.
     * 
     * - Type: Integer
     * - Required: Yes
     * - Minimum: 1
     * - Validation: Must be positive
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Unit price of the product at time of adding to cart.
     * 
     * Stored as snapshot because product price may change.
     * Shows the price that was current when item was added.
     * 
     * - Type: BigDecimal
     * - Precision: 12 digits total
     * - Scale: 2 decimal places (cents)
     * - Example: 999.99
     * - Required: Yes
     */
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    /**
     * Total price for this item (price * quantity).
     * 
     * - Type: BigDecimal
     * - Precision: 19 digits total
     * - Scale: 2 decimal places (cents)
     * - Example: quantity=2, price=100.00 => totalPrice=200.00
     * - Required: Yes
     * - Calculated: price * quantity
     */
    @Column(name = "total_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPrice;

    /**
     * Timestamp when the item was added to cart.
     * 
     * - Type: LocalDateTime
     * - Auto-set: Yes (at creation)
     * - Updatable: No (immutable after creation)
     * - Format: ISO-8601 (YYYY-MM-DD HH:mm:ss)
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of the last update to this item.
     * 
     * Updated when quantity changes.
     * 
     * - Type: LocalDateTime
     * - Auto-updated: Yes
     * - Format: ISO-8601 (YYYY-MM-DD HH:mm:ss)
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Reference to the parent Cart entity.
     * 
     * Relationship:
     * - Type: Many-to-One
     * - Fetch: LAZY
     * - Optional: No (required)
     * - Foreign Key: cart_id
     * 
     * @see Cart
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    @ToString.Exclude
    private Cart cart;

    /**
     * Lifecycle method: Set createdAt and updatedAt before persisting.
     */
    @jakarta.persistence.PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (totalPrice == null) {
            recalculateTotalPrice();
        }
    }

    /**
     * Lifecycle method: Update updatedAt and recalculate totalPrice before updating.
     */
    @jakarta.persistence.PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        recalculateTotalPrice();
    }

    /**
     * Recalculates total price based on quantity and unit price.
     * 
     * Formula: totalPrice = price * quantity
     */
    public void recalculateTotalPrice() {
        if (this.price != null && this.quantity != null) {
            this.totalPrice = this.price.multiply(BigDecimal.valueOf(this.quantity));
        }
    }
}
