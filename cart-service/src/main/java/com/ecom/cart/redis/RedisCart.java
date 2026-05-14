package com.ecom.cart.redis;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Redis Cart - In-memory representation of shopping cart.
 * 
 * Used for:
 * - Fast read/write operations
 * - Active cart storage
 * - Temporary persistence with TTL
 * 
 * Features:
 * - TTL (Time To Live): 7 days (604800 seconds)
 * - Auto-expiration: Cart expires after 7 days of inactivity
 * - Fast access: All operations use Redis hashing
 * 
 * Storage Strategy:
 * - Redis Key: cart:{userId}
 * - Data Structure: Redis Hash
 * - TTL: 7 days (604800 seconds)
 * 
 * Example Redis Key: cart:123e4567-e89b-12d3-a456-426614174000
 * 
 * Design Patterns:
 * - Value Object: Represents cart state in Redis
 * - Serialization: Implements Serializable for Redis storage
 * 
 * Lombok Annotations:
 * - @Data: Generates getter, setter, toString, equals, hashCode
 * - @NoArgsConstructor: Default constructor for deserialization
 * - @AllArgsConstructor: Constructor with all fields
 * - @Builder: Builder pattern for easy construction
 */
@RedisHash(value = "cart", timeToLive = 604800)  // 7 days TTL
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedisCart implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * User identifier (Redis key).
     * 
     * - Type: String (UUID as string)
     * - Primary Key in Redis
     * - Unique identifier for cart storage
     */
    @Id
    private String userId;

    /**
     * Cart identifier.
     *
     * - Type: String (UUID as string)
     * - Unique identifier for the cart
     */
    private String cartId;

    /**
     * Current status of the cart.
     *
     * - Type: String (CartStatus enum name)
     * - Values: ACTIVE, ABANDONED, CONVERTED, EXPIRED
     */
    private String status;

    /**
     * Total cart amount.
     *
     * - Type: BigDecimal
     * - Default: 0.00
     * - Updated on every item change
     */
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /**
     * List of items in the cart.
     * 
     * - Type: List of RedisCartItem
     * - Embedded in Redis Hash
     * 
     * @see RedisCartItem
     */
    private List<RedisCartItem> items;

    /**
     * Timestamp when cart was created in Redis.
     * 
     * - Type: LocalDateTime
     * - Set once at creation
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp of last update.
     * 
     * - Type: LocalDateTime
     * - Updated on every modification
     */
    private LocalDateTime updatedAt;
}
