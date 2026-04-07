package com.ecom.product.service;

import java.util.List;
import java.util.UUID;

import com.ecom.product.dto.request.BrandCreateRequest;
import com.ecom.product.dto.request.BrandUpdateRequest;
import com.ecom.product.dto.response.BrandResponse;

/**
 * Service interface for Brand operations.
 *
 * Defines business logic for brand management.
 */
public interface BrandService {

    /**
     * Creates a new brand.
     *
     * @param request the brand creation request
     * @param tenantId the tenant ID
     * @return the created brand response
     */
    BrandResponse createBrand(BrandCreateRequest request, String tenantId);

    /**
     * Retrieves all brands for the specified tenant.
     *
     * @param tenantId the tenant ID
     * @return list of brands
     */
    List<BrandResponse> getAllBrands(String tenantId);

    /**
     * Retrieves a brand by ID and tenant.
     *
     * @param id the brand ID
     * @param tenantId the tenant ID
     * @return the brand response
     */
    BrandResponse getBrandById(UUID id, String tenantId);

    /**
     * Retrieves a brand by code and tenant.
     *
     * @param code the brand code
     * @param tenantId the tenant ID
     * @return the brand response
     */
    BrandResponse getBrandByCode(String code, String tenantId);
    
    /**
     * Updates an existing brand.
     *
     * @param id the brand ID
     * @param request the brand update request
     * @param tenantId the tenant ID
     * @return the updated brand response
     */
    BrandResponse updateBrand(UUID id, BrandUpdateRequest request, String tenantId);

    /**
     * Deletes a brand by ID and tenant.
     *
     * @param id the brand ID
     * @param tenantId the tenant ID
     */
    void deleteBrand(UUID id, String tenantId);
}
