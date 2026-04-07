package com.ecom.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for updating an existing product.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateRequest {

    /**
     * Display name of the product.
     */
    @NotBlank(message = "Product name is required")
    private String name;

    /**
     * Optional description of the product.
     */
    private String description;

    /**
     * Category ID this product belongs to.
     */
    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    /**
     * Optional brand ID for this product.
     */
    private UUID brandId;

    /**
     * Unit of measure ID for this product.
     */
    @NotNull(message = "Unit of measure ID is required")
    private UUID unitOfMeasureId;

    /**
     * Price of the product.
     */
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;

    /**
     * Indicates if the product is active.
     */
    @NotNull(message = "Is active flag is required")
    private Boolean isActive;
}
