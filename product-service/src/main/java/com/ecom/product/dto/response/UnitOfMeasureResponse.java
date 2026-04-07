package com.ecom.product.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for unit of measure entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitOfMeasureResponse {

    /**
     * Unique identifier for the unit of measure.
     */
    private UUID id;

    /**
     * Unique code for the unit of measure.
     */
    private String code;

    /**
     * Display name of the unit of measure.
     */
    private String name;

    /**
     * Timestamp when the unit of measure was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the unit of measure was last updated.
     */
    private LocalDateTime updatedAt;
}
