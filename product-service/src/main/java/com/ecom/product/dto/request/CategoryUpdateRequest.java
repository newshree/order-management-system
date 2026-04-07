package com.ecom.product.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing category.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryUpdateRequest {

    /**
     * Display name of the category.
     */
    @NotBlank(message = "Category name is required")
    private String name;

    /**
     * Optional parent category ID for hierarchical categorization.
     */
    private UUID parentCategoryId;

    /**
     * Optional description of the category.
     */
    private String description;
}
