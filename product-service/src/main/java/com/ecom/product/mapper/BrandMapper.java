package com.ecom.product.mapper;

import com.ecom.product.dto.request.BrandCreateRequest;
import com.ecom.product.dto.request.BrandUpdateRequest;
import com.ecom.product.dto.response.BrandResponse;
import com.ecom.product.entity.Brand;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mapper for Brand entity and DTOs.
 *
 * Handles conversion between Brand entity and request/response DTOs.
 */
@Component
public class BrandMapper {

    /**
     * Maps a BrandCreateRequest to a Brand entity.
     *
     * @param request the create request DTO
     * @param tenantId the tenant ID
     * @return the mapped Brand entity
     */
    public Brand mapToEntity(BrandCreateRequest request, UUID tenantId) {
        return Brand.builder()
                .tenantId(tenantId)
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Maps a BrandUpdateRequest to an existing Brand entity.
     *
     * @param request the update request DTO
     * @param brand the existing Brand entity
     * @return the updated Brand entity
     */
    public Brand mapToEntity(BrandUpdateRequest request, Brand brand) {
        brand.setName(request.getName());
        brand.setDescription(request.getDescription());
        brand.setUpdatedAt(LocalDateTime.now());
        return brand;
    }

    /**
     * Maps a Brand entity to a BrandResponse DTO.
     *
     * @param brand the Brand entity
     * @return the mapped Brand response DTO
     */
    public BrandResponse mapToResponse(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .code(brand.getCode())
                .name(brand.getName())
                .description(brand.getDescription())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .build();
    }
}
