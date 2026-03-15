package com.ecom.ordersystem.orderservice.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * OrderItem - Domain model representing a line item in an order.
 * 
 * Represents individual products ordered as part of an Order.
 * Each OrderItem tracks product details, quantity, and pricing at the time of order.
 * 
 * Database Mapping:
 * - Table Name: order_items
 * - Primary Key: item_id (UUID)
 * - Foreign Key: order_id (references orders table)
 * 
 * Relationship:
 * - Many-to-One with Order
 * - Child entity in Order aggregate
 * 
 * Design Patterns:
 * - Entity Pattern: JPA entity with ORM mapping
 * - Value Object: Encapsulates product snapshot and pricing
 * 
 * Features:
 * - Maintains product snapshot (name, price) at order time
 * - Prevents product price changes from affecting historical orders
 * - Calculates total price as quantity × unit_price
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItem {

    /**
     * Unique identifier for the order item.
     * 
     * - Type: UUID
     * - Generated: Automatically via database
     * - Primary Key: Yes
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "item_id")
    private UUID id;

    /**
     * Reference to the parent Order entity.
     * 
     * Many-to-One relationship: Multiple items can belong to one order.
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
     * Product identifier from the Product Service.
     * 
     * Reference to external product entity (microservice architecture).
     * Enables product lookup and inventory management.
     * 
     * - Type: UUID
     * - Required: Yes
     * - Foreign Reference: Product service
     */
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    /**
     * Product name snapshot at time of order.
     * 
     * Stores the product name as it was at the time of order placement.
     * Prevents product name changes from affecting historical order data.
     * 
     * - Type: String
     * - Length: Max 255 characters
     * - Required: Yes
     * - Purpose: Historical accuracy and audit trail
     */
    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    /**
     * Quantity of this product ordered.
     * 
     * Represents how many units of the product were ordered.
     * Used with unit price to calculate total price.
     * 
     * - Type: Integer
     * - Required: Yes
     * - Constraint: Should be > 0
     * - Example: 5 (meaning 5 units)
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Unit price at time of order (per product).
     * 
     * Stores the price of a single unit at the time order was placed.
     * Historical snapshot prevents price changes from affecting past orders.
     * 
     * - Type: BigDecimal
     * - Precision: 19 digits total
     * - Scale: 2 decimal places (cents)
     * - Required: Yes
     * - Example: 99.99
     */
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    /**
     * Total price for this line item (quantity × unit_price).
     *
     * Calculated field: quantity multiplied by unit price.
     * Denormalized for query performance and historical accuracy.
     *
     * - Type: BigDecimal
     * - Precision: 19 digits total
     * - Scale: 2 decimal places (cents)
     * - Required: Yes
     * - Calculation: quantity × unitPrice
     * - Example: 5 × 99.99 = 499.95
     */
//    @Column(name = "total_price", nullable = false, precision = 19, scale = 2)
//    private BigDecimal totalPrice;
}

