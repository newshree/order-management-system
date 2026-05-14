package com.ecom.cart.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.ecom.cart.enums.CartStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CartResponse - Response DTO for complete cart data in API responses.
 * 
 * Represents the complete state of a user's shopping cart.
 * 
 * Usage:
 * - GET /api/cart - Retrieve cart
 * - POST /api/cart/items - Add item response
 * - All cart-related API responses
 * 
 * Example Response:
 * {
 *   "cartId": "550e8400-e29b-41d4-a716-446655440002",
 *   "userId": "550e8400-e29b-41d4-a716-446655440003",
 *   "status": "ACTIVE",
 *   "totalAmount": 1999.98,
 *   "items": [...],
 *   "createdAt": "2026-05-13T10:30:00",
 *   "updatedAt": "2026-05-13T10:30:00"
 * }
 * 
 * Design Pattern:
 * - Data Transfer Object (DTO) Pattern
 * - Response data contract
 * - Encapsulation of cart entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

    /**
     * Unique identifier of the cart.
     * 
     * - Type: UUID
     */
    private UUID cartId;

    /**
     * User identifier who owns this cart.
     * 
     * - Type: UUID
     */
    private UUID userId;

    /**
     * Current status of the cart.
     * 
     * Values: ACTIVE, ABANDONED, CONVERTED, EXPIRED
     * 
     * - Type: CartStatus enum
     */
    private CartStatus status;

    /**
     * Total cart amount (sum of all items).
     * 
     * - Type: BigDecimal
     * - Precision: 2 decimal places
     */
    private BigDecimal totalAmount;

    /**
     * List of items in the cart.
     * 
     * - Type: List of CartItemResponse
     */
    private List<CartItemResponse> items;

    /**
     * Timestamp when the cart was created.
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
