package com.ecom.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for inventory transaction details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    /**
     * Unique identifier for the transaction.
     */
    private UUID id;

    /**
     * Product ID associated with the transaction.
     */
    private String productId;

    /**
     * Order ID associated with the transaction (if applicable).
     */
    private String orderId;

    /**
     * Quantity changed in this transaction.
     */
    private Integer quantity;

    /**
     * Type of transaction.
     */
    private String transactionType;

    /**
     * Timestamp when the transaction occurred.
     */
    private LocalDateTime createdAt;
}

