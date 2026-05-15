package com.ecom.cart.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CheckoutSummaryResponse - Response DTO for checkout summary data.
 * 
 * Provides comprehensive pricing information for checkout process.
 * Includes subtotal, tax, delivery charge, and final amount.
 * 
 * API Endpoint: GET /api/cart/checkout
 * 
 * Example Response:
 * {
 *   "items": [...],
 *   "subtotal": 1999.98,
 *   "tax": 199.99,
 *   "deliveryCharge": 50.00,
 *   "totalAmount": 2249.97,
 *   "finalAmount": 2249.97
 * }
 * 
 * Design Pattern:
 * - Data Transfer Object (DTO) Pattern
 * - Response data contract
 * - Aggregated pricing information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutSummaryResponse {

    /**
     * List of items being checked out.
     * 
     * - Type: List of CartItemResponse
     */
    private List<CartItemResponse> items;

    /**
     * Subtotal of all items (before tax and delivery).
     * 
     * - Type: BigDecimal
     * - Precision: 2 decimal places
     * - Formula: Sum of all item total prices
     */
    private BigDecimal subtotal;

    /**
     * Tax amount to be charged.
     * 
     * - Type: BigDecimal
     * - Precision: 2 decimal places
     * - Calculated: subtotal * TAX_RATE (typically 10%)
     */
    private BigDecimal tax;

    /**
     * Delivery/Shipping charge.
     * 
     * - Type: BigDecimal
     * - Precision: 2 decimal places
     * - Fixed or calculated based on location
     */
    private BigDecimal deliveryCharge;

    /**
     * Total amount (subtotal + tax + delivery).
     * 
     * - Type: BigDecimal
     * - Precision: 2 decimal places
     * - Formula: subtotal + tax + deliveryCharge
     */
    private BigDecimal totalAmount;

    /**
     * Final amount to be paid by customer.
     * 
     * - Type: BigDecimal
     * - Precision: 2 decimal places
     * - Same as totalAmount
     */
    private BigDecimal finalAmount;
}
