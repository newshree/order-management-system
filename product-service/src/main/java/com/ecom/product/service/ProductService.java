package com.ecom.product.service;

import java.util.List;
import java.util.UUID;

import com.ecom.product.dto.request.ProductCreateRequest;
import com.ecom.product.dto.request.ProductUpdateRequest;
import com.ecom.product.dto.response.ProductResponse;

/**
 * Service interface for Product operations.
 *
 * Defines business logic for product management.
 */
public interface ProductService {

    /**
     * Creates a new product.
     *
     * @param request the product creation request
     * @param tenantId the tenant ID
     * @return the created product response
     */
    ProductResponse createProduct(ProductCreateRequest request, String tenantId);

    /**
     * Retrieves all products for the specified tenant.
     *
     * @param tenantId the tenant ID
     * @return list of products
     */
    List<ProductResponse> getAllProducts(String tenantId);

    /**
     * Retrieves a product by ID and tenant.
     *
     * @param id the product ID
     * @param tenantId the tenant ID
     * @return the product response
     */
    ProductResponse getProductById(UUID id, String tenantId);

    /**
     * Retrieves all products in a category for a tenant.
     *
     * @param categoryId the category ID
     * @param tenantId the tenant ID
     * @return list of products in the category
     */
    List<ProductResponse> getProductsByCategory(UUID categoryId, String tenantId);

    /**
     * Retrieves all active products for the specified tenant.
     *
     * @param tenantId the tenant ID
     * @return list of active products
     */
    List<ProductResponse> getActiveProducts(String tenantId);

    /**
     * Retrieves a product by code and tenant.
     *
     * @param code the product code
     * @param tenantId the tenant ID
     * @return the product response
     */
    ProductResponse getProductByCode(String code, String tenantId);

    /**
     * Creates multiple products in bulk for a tenant.
     *
     * @param requests list of product creation requests
     * @param tenantId the tenant ID
     * @return list of created product responses
     */
    List<ProductResponse> createBulkProducts(List<ProductCreateRequest> requests, String tenantId);

    /**
     * Activates multiple products for a tenant.
     *
     * @param productIds list of product IDs to activate
     * @param tenantId the tenant ID
     * @return list of activated product responses
     */
    List<ProductResponse> activateProducts(List<UUID> productIds, String tenantId);

    /**
     * Deactivates multiple products for a tenant.
     *
     * @param productIds list of product IDs to deactivate
     * @param tenantId the tenant ID
     * @return list of deactivated product responses
     */
    List<ProductResponse> deactivateProducts(List<UUID> productIds, String tenantId);
  
    /**
     * Updates an existing product.
     *
     * @param id the product ID
     * @param request the product update request
     * @param tenantId the tenant ID
     * @return the updated product response
     */
    ProductResponse updateProduct(UUID id, ProductUpdateRequest request, String tenantId);
    
    /**
     * Deletes a product by ID and tenant.
     *
     * @param id the product ID
     * @param tenantId the tenant ID
     */
    void deleteProduct(UUID id, String tenantId);
}
