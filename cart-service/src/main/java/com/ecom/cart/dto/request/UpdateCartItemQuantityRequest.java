package com.ecom.cart.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UpdateCartItemQuantityRequest - Request DTO for updating item quantity in cart.
 * 
 * API Endpoint: PUT /api/cart/items/{itemId}
 * 
 * Example Request:
 * {
 *   "quantity": 5
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
public class UpdateCartItemQuantityRequest {

    /**
     * New quantity for the cart item.
     * 
     * - Type: Integer
     * - Required: Yes
     * - Minimum: 1
     * - Validation: @Positive (ensures value > 0)
     */
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}
