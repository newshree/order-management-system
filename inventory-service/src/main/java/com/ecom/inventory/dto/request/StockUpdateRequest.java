package com.ecom.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for adding/updating inventory stock.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockUpdateRequest {

    /**
     * Product ID for which stock is being added.
     */
    @NotBlank(message = "Product ID cannot be blank")
    private String productId;

    /**
     * Quantity to add to the inventory.
     */
    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}

