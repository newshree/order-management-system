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

import com.ecom.product.dto.request.BrandCreateRequest;
import com.ecom.product.dto.request.BrandUpdateRequest;
import com.ecom.product.dto.response.ApiResponse;
import com.ecom.product.dto.response.BrandResponse;
import com.ecom.product.service.BrandService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Brand management.
 *
 * Provides endpoints for CRUD operations on brands.
 */
@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;
    private static final String TENANT_ID = "2f34e34a-a524-4aff-9702-35e6f8c6f9d1";

    /**
     * Creates a new brand.
     *
     * @param request the brand creation request
     * @param tenantId the tenant ID from request header
     * @return API response with the created brand
     */
    @PostMapping("/createBrand")
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(
            @Valid @RequestBody BrandCreateRequest request,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        BrandResponse brand = brandService.createBrand(request, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<BrandResponse>builder()
                .success(true)
                .data(brand)
                .message("Brand created successfully")
                .build());
    }

    /**
     * Retrieves all brands.
     *
     * @param tenantId the tenant ID from request header
     * @return API response with list of brands
     */
    @GetMapping("/getAllBrands")
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAllBrands(
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        List<BrandResponse> brands = brandService.getAllBrands(tenantId);
        return ResponseEntity.ok(ApiResponse.<List<BrandResponse>>builder()
                .success(true)
                .data(brands)
                .message("Brands retrieved successfully")
                .build());
    }

    /**
     * Retrieves a brand by ID.
     *
     * @param id the brand ID
     * @param tenantId the tenant ID from request header
     * @return API response with the brand
     */
    @GetMapping("/getBrandById/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrandById(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        BrandResponse brand = brandService.getBrandById(id, tenantId);
        return ResponseEntity.ok(ApiResponse.<BrandResponse>builder()
                .success(true)
                .data(brand)
                .message("Brand retrieved successfully")
                .build());
    }

        /**
     * Retrieves a brand by code.
     *
     * @param code the brand code
     * @param tenantId the tenant ID from request header
     * @return API response with the brand
     */
    @GetMapping("/getBrandByCode/{code}")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrandByCode(
            @PathVariable String code,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        BrandResponse brand = brandService.getBrandByCode(code, tenantId);
        return ResponseEntity.ok(ApiResponse.<BrandResponse>builder()
                .success(true)
                .data(brand)
                .message("Brand retrieved successfully")
                .build());
    }
    
    /**
     * Updates an existing brand.
     *
     * @param id the brand ID
     * @param request the brand update request
     * @param tenantId the tenant ID from request header
     * @return API response with the updated brand
     */
    @PutMapping("/updateBrand/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(
            @PathVariable UUID id,
            @Valid @RequestBody BrandUpdateRequest request,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        BrandResponse brand = brandService.updateBrand(id, request, tenantId);
        return ResponseEntity.ok(ApiResponse.<BrandResponse>builder()
                .success(true)
                .data(brand)
                .message("Brand updated successfully")
                .build());
    }

    /**
     * Deletes a brand.
     *
     * @param id the brand ID
     * @param tenantId the tenant ID from request header
     * @return API response indicating successful deletion
     */
    @DeleteMapping("/deleteBrand/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        brandService.deleteBrand(id, tenantId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Brand deleted successfully")
                .build());
    }
}
