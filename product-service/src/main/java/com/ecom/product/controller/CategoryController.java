package com.ecom.product.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.product.dto.request.CategoryCreateRequest;
import com.ecom.product.dto.request.CategoryUpdateRequest;
import com.ecom.product.dto.response.ApiResponse;
import com.ecom.product.dto.response.CategoryResponse;
import com.ecom.product.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Category management.
 *
 * Provides endpoints for CRUD operations on categories.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private static final String TENANT_ID = "2f34e34a-a524-4aff-9702-35e6f8c6f9d1";

    /**
     * Creates a new category.
     *
     * @param request the category creation request
     * @param tenantId the tenant ID from request header
     * @return API response with the created category
     */
    @PostMapping("/createCategory")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryCreateRequest request,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        CategoryResponse category = categoryService.createCategory(request, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<CategoryResponse>builder()
                .success(true)
                .data(category)
                .message("Category created successfully")
                .build());
    }

    /**
     * Retrieves all categories.
     *
     * @param tenantId the tenant ID from request header
     * @return API response with list of categories
     */
    @GetMapping("/getAllCategories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        List<CategoryResponse> categories = categoryService.getAllCategories(tenantId);
        return ResponseEntity.ok(ApiResponse.<List<CategoryResponse>>builder()
                .success(true)
                .data(categories)
                .message("Categories retrieved successfully")
                .build());
    }

    /**
     * Retrieves a category by ID.
     *
     * @param id the category ID
     * @param tenantId the tenant ID from request header
     * @return API response with the category
     */
    @GetMapping("/getCategoryById/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        CategoryResponse category = categoryService.getCategoryById(id, tenantId);
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .success(true)
                .data(category)
                .message("Category retrieved successfully")
                .build());
    }

    /**
     * Retrieves a category by code.
     *
     * @param code the category code
     * @param tenantId the tenant ID from request header
     * @return API response with the category
     */
    @GetMapping("/getCategoryByCode/{code}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryByCode(
            @PathVariable String code,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        CategoryResponse category = categoryService.getCategoryByCode(code, tenantId);
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .success(true)
                .data(category)
                .message("Category retrieved successfully")
                .build());
    }

    /**
     * Updates an existing category.
     *
     * @param id the category ID
     * @param request the category update request
     * @param tenantId the tenant ID from request header
     * @return API response with the updated category
     */
    @PutMapping("/updateCategory/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryUpdateRequest request,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        CategoryResponse category = categoryService.updateCategory(id, request, tenantId);
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .success(true)
                .data(category)
                .message("Category updated successfully")
                .build());
    }

    /**
     * Deletes a category.
     *
     * @param id the category ID
     * @param tenantId the tenant ID from request header
     * @return API response indicating successful deletion
     */
    @DeleteMapping("/deleteCategory/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        categoryService.deleteCategory(id, tenantId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Category deleted successfully")
                .build());
    }
}
