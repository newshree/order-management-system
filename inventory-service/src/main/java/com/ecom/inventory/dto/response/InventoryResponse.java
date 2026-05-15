package com.ecom.inventory.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for inventory details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {

    /**
     * Unique identifier for the inventory record.
     */
    private UUID id;

    /**
     * Product ID associated with this inventory.
     */
    private String productId;

    /**
     * Available quantity that can be ordered.
     */
    private Integer availableQuantity;

    /**
     * Reserved quantity pending payment.
     */
    private Integer reservedQuantity;

    /**
     * Total quantity (available + reserved).
     */
    private Integer totalQuantity;

    /**
     * Timestamp when the record was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the record was last updated.
     */
    private LocalDateTime updatedAt;

    // /**
    //  * Converts an Inventory entity to InventoryResponse DTO.
    //  * 
    //  * Builds complete response from all inventory details, including calculated total quantity.
    //  * 
    //  * @param inventory the Inventory entity to convert
    //  * @return InventoryResponse DTO built from entity
    //  */
    // public static InventoryResponse from(Inventory inventory) {
    //     Integer totalQuantity = inventory.getAvailableQuantity() + inventory.getReservedQuantity();

    //     return InventoryResponse.builder()
    //         .id(inventory.getId())
    //         .productId(inventory.getProductId())
    //         .availableQuantity(inventory.getAvailableQuantity())
    //         .reservedQuantity(inventory.getReservedQuantity())
    //         .totalQuantity(totalQuantity)
    //         .createdAt(inventory.getCreatedAt())
    //         .updatedAt(inventory.getUpdatedAt())
    //         .build();
    // }
}

