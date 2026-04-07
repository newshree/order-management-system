package com.ecom.product.mapper;

import com.ecom.product.dto.request.CategoryCreateRequest;
import com.ecom.product.dto.request.CategoryUpdateRequest;
import com.ecom.product.dto.response.CategoryResponse;
import com.ecom.product.entity.Category;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mapper for Category entity and DTOs.
 *
 * Handles conversion between Category entity and request/response DTOs.
 */
@Component
public class CategoryMapper {

    /**
     * Maps a CategoryCreateRequest to a Category entity.
     *
     * @param request the create request DTO
     * @param tenantId the tenant ID
     * @return the mapped Category entity
     */
    public Category mapToEntity(CategoryCreateRequest request, UUID tenantId) {
        return Category.builder()
                .tenantId(tenantId)
                .code(request.getCode())
                .name(request.getName())
                .parentCategoryId(request.getParentCategoryId())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Maps a CategoryUpdateRequest to an existing Category entity.
     *
     * @param request the update request DTO
     * @param category the existing Category entity
     * @return the updated Category entity
     */
    public Category mapToEntity(CategoryUpdateRequest request, Category category) {
        category.setName(request.getName());
        category.setParentCategoryId(request.getParentCategoryId());
        category.setDescription(request.getDescription());
        category.setUpdatedAt(LocalDateTime.now());
        return category;
    }

    /**
     * Maps a Category entity to a CategoryResponse DTO.
     *
     * @param category the Category entity
     * @return the mapped Category response DTO
     */
    public CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .parentCategoryId(category.getParentCategoryId())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
