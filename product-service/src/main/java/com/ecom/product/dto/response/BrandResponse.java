package com.ecom.product.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for brand entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandResponse {

    /**
     * Unique identifier for the brand.
     */
    private UUID id;

    /**
     * Unique code for the brand.
     */
    private String code;

    /**
     * Display name of the brand.
     */
    private String name;

    /**
     * Optional description of the brand.
     */
    private String description;

    /**
     * Timestamp when the brand was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the brand was last updated.
     */
    private LocalDateTime updatedAt;
}
