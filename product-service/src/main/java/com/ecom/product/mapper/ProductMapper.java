package com.ecom.product.mapper;

import com.ecom.product.dto.request.ProductCreateRequest;
import com.ecom.product.dto.request.ProductUpdateRequest;
import com.ecom.product.dto.response.ProductResponse;
import com.ecom.product.entity.Product;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mapper for Product entity and DTOs.
 *
 * Handles conversion between Product entity and request/response DTOs.
 */
@Component
public class ProductMapper {

    /**
     * Maps a ProductCreateRequest to a Product entity.
     *
     * @param request the create request DTO
     * @param tenantId the tenant ID
     * @return the mapped Product entity
     */
    public Product mapToEntity(ProductCreateRequest request, UUID tenantId) {
        return Product.builder()
                .tenantId(tenantId)
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .categoryId(request.getCategoryId())
                .brandId(request.getBrandId())
                .unitOfMeasureId(request.getUnitOfMeasureId())
                .price(request.getPrice())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Maps a ProductUpdateRequest to an existing Product entity.
     *
     * @param request the update request DTO
     * @param product the existing Product entity
     * @return the updated Product entity
     */
    public Product mapToEntity(ProductUpdateRequest request, Product product) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategoryId(request.getCategoryId());
        product.setBrandId(request.getBrandId());
        product.setUnitOfMeasureId(request.getUnitOfMeasureId());
        product.setPrice(request.getPrice());
        product.setIsActive(request.getIsActive());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }

    /**
     * Maps a Product entity to a ProductResponse DTO.
     *
     * @param product the Product entity
     * @return the mapped Product response DTO
     */
    public ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategoryId())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .brandId(product.getBrandId())
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .unitOfMeasureId(product.getUnitOfMeasureId())
                .unitOfMeasureName(product.getUnitOfMeasure() != null ? product.getUnitOfMeasure().getName() : null)
                .price(product.getPrice())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
