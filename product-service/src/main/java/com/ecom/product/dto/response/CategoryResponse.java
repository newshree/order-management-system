package com.ecom.product.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for category entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse {

    /**
     * Unique identifier for the category.
     */
    private UUID id;

    /**
     * Unique code for the category.
     */
    private String code;

    /**
     * Display name of the category.
     */
    private String name;

    /**
     * Optional parent category ID.
     */
    private UUID parentCategoryId;

    /**
     * Optional description of the category.
     */
    private String description;

    /**
     * Timestamp when the category was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the category was last updated.
     */
    private LocalDateTime updatedAt;
}
