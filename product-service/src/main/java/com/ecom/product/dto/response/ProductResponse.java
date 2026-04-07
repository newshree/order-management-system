package com.ecom.product.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for product entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {

    /**
     * Unique identifier for the product.
     */
    private UUID id;

    /**
     * Unique code for the product.
     */
    private String code;

    /**
     * Display name of the product.
     */
    private String name;

    /**
     * Optional description of the product.
     */
    private String description;

    /**
     * Category ID this product belongs to.
     */
    private UUID categoryId;

    /**
     * Category name for this product.
     */
    private String categoryName;

    /**
     * Optional brand ID for this product.
     */
    private UUID brandId;

    /**
     * Brand name for this product.
     */
    private String brandName;

    /**
     * Unit of measure ID for this product.
     */
    private UUID unitOfMeasureId;

    /**
     * Unit of measure name for this product.
     */
    private String unitOfMeasureName;

    /**
     * Price of the product.
     */
    private BigDecimal price;

    /**
     * Indicates if the product is active.
     */
    private Boolean isActive;

    /**
     * Timestamp when the product was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the product was last updated.
     */
    private LocalDateTime updatedAt;
}
