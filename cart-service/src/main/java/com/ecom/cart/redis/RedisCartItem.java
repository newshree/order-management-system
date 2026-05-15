package com.ecom.cart.redis;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RedisCartItem - Represents a single item stored in Redis cart.
 * 
 * Embedded within RedisCart as a list of items.
 * 
 * Design Patterns:
 * - Value Object: Represents item state
 * - Data Transfer Object: Transferred between layers
 * - Serialization: Implements Serializable for Redis storage
 * 
 * Lombok Annotations:
 * - @Data: Generates getter, setter, toString, equals, hashCode
 * - @NoArgsConstructor: Default constructor for deserialization
 * - @AllArgsConstructor: Constructor with all fields
 * - @Builder: Builder pattern for easy construction
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedisCartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the cart item.
     * 
     * - Type: UUID as string
     * - Used for item identification and updates
     */
    private String itemId;

    /**
     * Product identifier (reference to Product Service).
     * 
     * - Type: UUID as string
     * - Used to fetch product details from external service
     */
    private String productId;

    /**
     * Product name (snapshot).
     * 
     * - Type: String
     * - Stored at time of adding to avoid external service calls
     */
    private String productName;

    /**
     * Quantity of this item.
     * 
     * - Type: Integer
     * - Minimum: 1
     * - Updated when quantity changes
     */
    private Integer quantity;

    /**
     * Unit price of the product (snapshot).
     * 
     * - Type: BigDecimal
     * - Stored at time of adding for historical reference
     */
    private BigDecimal price;

    /**
     * Total price for this item (price * quantity).
     *
     * - Type: BigDecimal
     * - Calculated as price * quantity
     */
    private BigDecimal totalPrice;

    /**
     * Timestamp when the item was created.
     *
     * - Type: LocalDateTime
     * - Set once at creation
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp of last update to this item.
     *
     * - Type: LocalDateTime
     * - Updated when quantity or price changes
     */
    private LocalDateTime updatedAt;
}
