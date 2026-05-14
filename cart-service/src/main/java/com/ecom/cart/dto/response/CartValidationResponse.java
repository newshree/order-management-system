package com.ecom.cart.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CartValidationResponse - Response DTO for cart validation results.
 * 
 * Provides detailed validation information about cart items.
 * Includes validation status and any validation errors or warnings.
 * 
 * API Endpoint: POST /api/cart/validate
 * 
 * Example Response:
 * {
 *   "isValid": true,
 *   "errorCount": 0,
 *   "warningCount": 1,
 *   "validationErrors": [],
 *   "validationWarnings": ["Item X price has increased"]
 * }
 * 
 * Design Pattern:
 * - Data Transfer Object (DTO) Pattern
 * - Response data contract
 * - Validation result aggregation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartValidationResponse {

    /**
     * Whether the cart is valid for checkout.
     * 
     * - Type: Boolean
     * - True: Cart can proceed to checkout
     * - False: Cart has validation errors
     */
    private Boolean isValid;

    /**
     * Count of validation errors found.
     * 
     * - Type: Integer
     * - Errors prevent checkout
     */
    @Builder.Default
    private Integer errorCount = 0;

    /**
     * Count of validation warnings found.
     * 
     * - Type: Integer
     * - Warnings are informational
     */
    @Builder.Default
    private Integer warningCount = 0;

    /**
     * List of validation error messages.
     * 
     * - Type: List of String
     * - Examples:
     *   - "Product X is no longer available"
     *   - "Product Y has insufficient stock"
     *   - "Product Z price has changed"
     */
    private List<String> validationErrors;

    /**
     * List of validation warning messages.
     * 
     * - Type: List of String
     * - Examples:
     *   - "Product X price has increased by 10%"
     *   - "Stock for Product Y is running low"
     */
    private List<String> validationWarnings;
}
