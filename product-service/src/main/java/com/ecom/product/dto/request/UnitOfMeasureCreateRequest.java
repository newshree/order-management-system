package com.ecom.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new unit of measure.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitOfMeasureCreateRequest {

    /**
     * Unique code for the unit of measure.
     */
    @NotBlank(message = "Unit of measure code is required")
    private String code;

    /**
     * Display name of the unit of measure.
     */
    @NotBlank(message = "Unit of measure name is required")
    private String name;
}
