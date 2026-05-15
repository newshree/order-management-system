package com.ecom.cart.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CartItemResponse - Response DTO for cart item data in API responses.
 * 
 * Represents a single item in the cart with all relevant information.
 * 
 * Usage:
 * - Included in CartResponse
 * - Shows item details to client
 * 
 * Example Response:
 * {
 *   "itemId": "550e8400-e29b-41d4-a716-446655440001",
 *   "productId": "550e8400-e29b-41d4-a716-446655440000",
 *   "productName": "Laptop",
 *   "quantity": 2,
 *   "price": 999.99,
 *   "totalPrice": 1999.98,
 *   "createdAt": "2026-05-13T10:30:00",
 *   "updatedAt": "2026-05-13T10:30:00"
 * }
 * 
 * Design Pattern:
 * - Data Transfer Object (DTO) Pattern
 * - Response data contract
 * - Encapsulation of entity details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {

    /**
     * Unique identifier of the cart item.
     * 
     * - Type: UUID
     */
    private UUID itemId;

    /**
     * Product identifier.
     * 
     * - Type: UUID
     * - Reference to Product Service
     */
    private UUID productId;

    /**
     * Product name (snapshot).
     * 
     * - Type: String
     * - Stored at time of adding
     */
    private String productName;

    /**
     * Quantity of this item in cart.
     * 
     * - Type: Integer
     */
    private Integer quantity;

    /**
     * Unit price of the product (snapshot).
     * 
     * - Type: BigDecimal
     * - Precision: 2 decimal places
     */
    private BigDecimal price;

    /**
     * Total price for this item (price * quantity).
     * 
     * - Type: BigDecimal
     * - Precision: 2 decimal places
     */
    private BigDecimal totalPrice;

    /**
     * Timestamp when item was added to cart.
     * 
     * - Type: LocalDateTime
     * - Format: ISO-8601
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp of last update.
     * 
     * - Type: LocalDateTime
     * - Format: ISO-8601
     */
    private LocalDateTime updatedAt;
}
