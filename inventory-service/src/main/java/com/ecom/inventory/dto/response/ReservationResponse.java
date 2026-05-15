package com.ecom.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for inventory reservation details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {

    /**
     * Unique identifier for the reservation.
     */
    private UUID id;

    /**
     * Order ID associated with this reservation.
     */
    private String orderId;

    /**
     * Product ID for this reservation.
     */
    private String productId;

    /**
     * Quantity reserved.
     */
    private Integer quantity;

    /**
     * Status of the reservation: RESERVED, COMMITTED, RELEASED.
     */
    private String status;

    /**
     * Timestamp when the reservation was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the reservation was last updated.
     */
    private LocalDateTime updatedAt;
}

