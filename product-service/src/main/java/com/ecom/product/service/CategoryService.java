package com.ecom.product.service;

import java.util.List;
import java.util.UUID;

import com.ecom.product.dto.request.CategoryCreateRequest;
import com.ecom.product.dto.request.CategoryUpdateRequest;
import com.ecom.product.dto.response.CategoryResponse;

/**
 * Service interface for Category operations.
 *
 * Defines business logic for category management.
 */
public interface CategoryService {

    /**
     * Creates a new category.
     *
     * @param request the category creation request
     * @param tenantId the tenant ID
     * @return the created category response
     */
    CategoryResponse createCategory(CategoryCreateRequest request, String tenantId);

    /**
     * Retrieves all categories for the specified tenant.
     *
     * @param tenantId the tenant ID
     * @return list of categories
     */
    List<CategoryResponse> getAllCategories(String tenantId);

    /**
     * Retrieves a category by ID and tenant.
     *
     * @param id the category ID
     * @param tenantId the tenant ID
     * @return the category response
     */
    CategoryResponse getCategoryById(UUID id, String tenantId);

    /**
     * Retrieves a category by code and tenant.
     *
     * @param code the category code
     * @param tenantId the tenant ID
     * @return the category response
     */
    CategoryResponse getCategoryByCode(String code, String tenantId);

    /**
     * Updates an existing category.
     *
     * @param id the category ID
     * @param request the category update request
     * @param tenantId the tenant ID
     * @return the updated category response
     */
    CategoryResponse updateCategory(UUID id, CategoryUpdateRequest request, String tenantId);
    
    /**
     * Deletes a category by ID and tenant.
     *
     * @param id the category ID
     * @param tenantId the tenant ID
     */
    void deleteCategory(UUID id, String tenantId);
}
