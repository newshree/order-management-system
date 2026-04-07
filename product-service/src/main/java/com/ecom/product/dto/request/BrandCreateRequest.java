package com.ecom.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new brand.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandCreateRequest {

    /**
     * Unique code for the brand.
     */
    @NotBlank(message = "Brand code is required")
    private String code;

    /**
     * Display name of the brand.
     */
    @NotBlank(message = "Brand name is required")
    private String name;

    /**
     * Optional description of the brand.
     */
    private String description;
}
