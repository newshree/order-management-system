package com.ecom.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for Inventory Stock request operations for an order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryStockRequest {

    /**
     * Product ID for which the stock is being updated.
     */
    @NotBlank(message = "Product ID cannot be blank")
    private String productId;


    /**
     * Quantity of the product to be reserved or released or committed.
     */
    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;


    /**
     *  Order ID associated with the inventory stock operation.
     *  This is used to track which order the inventory change is related to.
     */
    @NotBlank(message = "Order ID cannot be blank")
    private String orderId;
}

