package com.ecom.product.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.product.dto.request.CategoryCreateRequest;
import com.ecom.product.dto.request.CategoryUpdateRequest;
import com.ecom.product.dto.response.CategoryResponse;
import com.ecom.product.entity.Category;
import com.ecom.product.enums.ErrorCode;
import com.ecom.product.exception.BadRequestException;
import com.ecom.product.exception.ResourceNotFoundException;
import com.ecom.product.mapper.CategoryMapper;
import com.ecom.product.repository.CategoryRepository;
import com.ecom.product.service.CategoryService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of CategoryService.
 *
 * Provides business logic for category management operations.
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Creates a new category for the specified tenant.
     *
     * @param request the category creation request
     * @param tenantId the tenant ID
     * @return the created category response
     * @throws BadRequestException if category code already exists
     */
    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request, String tenantId) {
        UUID tenantUUID = UUID.fromString(tenantId);
        // Check if category code already exists
        if (categoryRepository.findByTenantIdAndCode(tenantUUID, request.getCode()).isPresent()) {
            throw new BadRequestException(ErrorCode.DUPLICATE_CATEGORY,
                    "Category with code '" + request.getCode() + "' already exists");
        }

        // Validate parent category if provided
        if (request.getParentCategoryId() != null) {
            parentCategoryValidation(request.getParentCategoryId(), tenantUUID);
        }

        Category category = categoryMapper.mapToEntity(request, tenantUUID);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.mapToResponse(savedCategory);
    }

    /**
     * Retrieves all categories for the specified tenant.
     *
     * @param tenantId the tenant ID
     * @return list of category responses
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories(String tenantId) {
        UUID tenantUUID = UUID.fromString(tenantId);
        return categoryRepository.findByTenantId(tenantUUID)
                .stream()
                .map(categoryMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a category by ID for the specified tenant.
     *
     * @param id the category ID
     * @param tenantId the tenant ID
     * @return the category response
     * @throws ResourceNotFoundException if category not found
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID id, String tenantId) {
        Category category = findCategory(id);
        validateTenant(tenantId, category);
        return categoryMapper.mapToResponse(category);
    }

    /**
     * Retrieves a category by code for the specified tenant.
     *
     * @param code the category code
     * @param tenantId the tenant ID
     * @return the category response
     * @throws ResourceNotFoundException if category not found
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryByCode(String code, String tenantId) {
        UUID tenantUUID = UUID.fromString(tenantId);
        Category category = categoryRepository.findByTenantIdAndCode(tenantUUID, code)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND,
                        "Category not found with code: " + code));

        return categoryMapper.mapToResponse(category);
    }

    /**
     * Updates an existing category for the specified tenant.
     *
     * @param id the category ID
     * @param request the category update request
     * @param tenantId the tenant ID
     * @return the updated category response
     * @throws ResourceNotFoundException if category not found
     */
    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID id, CategoryUpdateRequest request, String tenantId) {
        UUID tenantUUID = UUID.fromString(tenantId);
        Category category = findCategory(id);
        validateTenant(tenantId, category);

        // Validate parent category if provided
        if (request.getParentCategoryId() != null && !request.getParentCategoryId().equals(category.getId())) {
            parentCategoryValidation(request.getParentCategoryId(), tenantUUID);
        }

        Category updatedCategory = categoryMapper.mapToEntity(request, category);
        Category savedCategory = categoryRepository.save(updatedCategory);
        return categoryMapper.mapToResponse(savedCategory);
    }

    /**
     * Deletes a category by ID for the specified tenant.
     *
     * @param id the category ID
     * @param tenantId the tenant ID
     * @throws ResourceNotFoundException if category not found
     */
    @Override
    public void deleteCategory(UUID id, String tenantId) {
        Category category = findCategory(id);
        validateTenant(tenantId, category);
        categoryRepository.delete(category);
    }

    /**
     * The fetched brand actually belongs to the requesting tenant
     * Prevents cross-tenant data access
     * Enforces tenant isolation
     * 
     * @tenantId the tenant ID from request header
     * @category the category entity fetched from database
     */
    private void validateTenant(String tenantId, Category category) {
        UUID tenantUUID = UUID.fromString(tenantId);
        if (!category.getTenantId().equals(tenantUUID)) {
            throw new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND,
                    "Category with id '" + category.getId() + "' does not belong to the current tenant");
        }
    }

    /**
     * Validates that the parent category exists and belongs to the same tenant
     * Prevents cross-tenant data access
     * Enforces tenant isolation
     * 
     * @param parentCategoryId the parent category ID to validate
     * @param tenantId the tenant ID from request header
     */
    private void parentCategoryValidation(UUID parentCategoryId, UUID tenantUUID) {
        Category parentCategory = categoryRepository.findById(parentCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.INVALID_CATEGORY_ID,
                        "Parent category not found with id: " + parentCategoryId));

        if (!parentCategory.getTenantId().equals(tenantUUID)) {
            throw new BadRequestException(ErrorCode.INVALID_CATEGORY_ID,
                    "Parent category does not belong to the current tenant");
        }
    }

    /**
     * Helper method to find a category by ID and throw ResourceNotFoundException if not found.
     * 
     * @param id the category ID
     * @return the found category
     * @throws ResourceNotFoundException if category not found
     */
    private Category findCategory(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND,
                        "Category not found with id: " + id));
    }
}
