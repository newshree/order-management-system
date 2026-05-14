package com.ecom.cart.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AddItemToCartRequest - Request DTO for adding an item to cart.
 * 
 * API Endpoint: POST /api/cart/items
 * 
 * Example Request:
 * {
 *   "productId": "550e8400-e29b-41d4-a716-446655440000",
 *   "quantity": 2
 * }
 * 
 * Design Pattern:
 * - Data Transfer Object (DTO) Pattern
 * - Validation encapsulation
 * - Request data contract
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddItemToCartRequest {

    /**
     * Product identifier to add to cart.
     * 
     * - Type: UUID
     * - Required: Yes
     * - Must be a valid UUID
     * - Validation: @NotNull
     */
    @NotNull(message = "Product ID cannot be null")
    private UUID productId;

    /**
     * Quantity of the product to add.
     * 
     * - Type: Integer
     * - Required: Yes
     * - Minimum: 1
     * - Validation: @Positive (ensures value > 0)
     */
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}
