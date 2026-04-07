package com.ecom.product.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.product.dto.request.BrandCreateRequest;
import com.ecom.product.dto.request.BrandUpdateRequest;
import com.ecom.product.dto.response.BrandResponse;
import com.ecom.product.entity.Brand;
import com.ecom.product.enums.ErrorCode;
import com.ecom.product.exception.BadRequestException;
import com.ecom.product.exception.ResourceNotFoundException;
import com.ecom.product.mapper.BrandMapper;
import com.ecom.product.repository.BrandRepository;
import com.ecom.product.service.BrandService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of BrandService.
 *
 * Provides business logic for brand management operations.
 */
@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    /**
     * Creates a new brand for the specified tenant.
     *
     * @param request the brand creation request
     * @param tenantId the tenant ID
     * @return the created brand response
     * @throws BadRequestException if brand code already exists
     */
    @Override
    @Transactional
    public BrandResponse createBrand(BrandCreateRequest request, String tenantId) {
        UUID tenantUUID = UUID.fromString(tenantId);
        // Check if brand code already exists
        if (brandRepository.findByTenantIdAndCode(tenantUUID, request.getCode()).isPresent()) {
            throw new BadRequestException(ErrorCode.DUPLICATE_BRAND,
                    "Brand with code '" + request.getCode() + "' already exists");
        }

        Brand brand = brandMapper.mapToEntity(request, tenantUUID);
        Brand savedBrand = brandRepository.save(brand);
        return brandMapper.mapToResponse(savedBrand);
    }

    /**
     * Retrieves all brands for the specified tenant.
     *
     * @param tenantId the tenant ID
     * @return list of brand responses
     */
    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getAllBrands(String tenantId) {
        UUID tenantUUID = UUID.fromString(tenantId);
        return brandRepository.findByTenantId(tenantUUID)
                .stream()
                .map(brandMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a brand by ID for the specified tenant.
     *
     * @param id the brand ID
     * @param tenantId the tenant ID
     * @return the brand response
     * @throws ResourceNotFoundException if brand not found
     */
    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrandById(UUID id, String tenantId) {
        Brand brand = findBrand(id);
        tenantValidation(tenantId, brand);
        return brandMapper.mapToResponse(brand);
    }

    /**
     * Retrieves a brand by code for the specified tenant.
     *
     * @param code the brand code
     * @param tenantId the tenant ID
     * @return the brand response
     * @throws ResourceNotFoundException if brand not found
     */
    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrandByCode(String code, String tenantId) {
        UUID tenantUUID = UUID.fromString(tenantId);
        Brand brand = brandRepository.findByTenantIdAndCode(tenantUUID, code)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRAND_NOT_FOUND,
                        "Brand not found with code: " + code));

        return brandMapper.mapToResponse(brand);
    }

    /**
     * Updates an existing brand for the specified tenant.
     *
     * @param id the brand ID
     * @param request the brand update request
     * @param tenantId the tenant ID
     * @return the updated brand response
     * @throws ResourceNotFoundException if brand not found
     */
    @Override
    @Transactional
    public BrandResponse updateBrand(UUID id, BrandUpdateRequest request, String tenantId) {
        Brand brand = findBrand(id);
        tenantValidation(tenantId, brand);

        Brand updatedBrand = brandMapper.mapToEntity(request, brand);
        Brand savedBrand = brandRepository.save(updatedBrand);
        return brandMapper.mapToResponse(savedBrand);
    }

    /**
     * Deletes a brand by ID for the specified tenant.
     *
     * @param id the brand ID
     * @param tenantId the tenant ID
     * @throws ResourceNotFoundException if brand not found
     */
    @Override
    @Transactional
    public void deleteBrand(UUID id, String tenantId) {
        Brand brand = findBrand(id);
        tenantValidation(tenantId, brand);
        brandRepository.delete(brand);
    }

    /**
     * The fetched brand actually belongs to the requesting tenant
     * Prevents cross-tenant data access
     * Enforces tenant isolation
     * 
     * @tenantId the tenant ID from request header
     * @brand the brand entity fetched from database
     */
    private void tenantValidation(String tenantId, Brand brand) {
        UUID tenantUUID = UUID.fromString(tenantId);
        if (!brand.getTenantId().equals(tenantUUID)) {
            throw new ResourceNotFoundException(ErrorCode.BRAND_NOT_FOUND,
                    "Brand with id '" + brand.getId() + "' does not belong to the current tenant");
        }
    }

    /**
     * Helper method to find a brand by ID and throw ResourceNotFoundException if not found.
     * 
     * @param id the brand ID
     * @return the found brand
     * @throws ResourceNotFoundException if brand not found
     */
    private Brand findBrand(UUID id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.BRAND_NOT_FOUND,
                        "Brand not found with id: " + id));
    }
}
