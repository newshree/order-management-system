package com.ecom.product.mapper;

import com.ecom.product.dto.request.UnitOfMeasureCreateRequest;
import com.ecom.product.dto.request.UnitOfMeasureUpdateRequest;
import com.ecom.product.dto.response.UnitOfMeasureResponse;
import com.ecom.product.entity.UnitOfMeasure;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper for UnitOfMeasure entity and DTOs.
 *
 * Handles conversion between UnitOfMeasure entity and request/response DTOs.
 */
@Component
public class UnitOfMeasureMapper {

    /**
     * Maps a UnitOfMeasureCreateRequest to a UnitOfMeasure entity.
     *
     * @param request the create request DTO
     * @return the mapped UnitOfMeasure entity
     */
    public UnitOfMeasure mapToEntity(UnitOfMeasureCreateRequest request) {
        return UnitOfMeasure.builder()
                .code(request.getCode())
                .name(request.getName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Maps a UnitOfMeasureUpdateRequest to an existing UnitOfMeasure entity.
     *
     * @param request the update request DTO
     * @param unitOfMeasure the existing UnitOfMeasure entity
     * @return the updated UnitOfMeasure entity
     */
    public UnitOfMeasure mapToEntity(UnitOfMeasureUpdateRequest request, UnitOfMeasure unitOfMeasure) {
        unitOfMeasure.setName(request.getName());
        unitOfMeasure.setUpdatedAt(LocalDateTime.now());
        return unitOfMeasure;
    }

    /**
     * Maps a UnitOfMeasure entity to a UnitOfMeasureResponse DTO.
     *
     * @param unitOfMeasure the UnitOfMeasure entity
     * @return the mapped UnitOfMeasure response DTO
     */
    public UnitOfMeasureResponse mapToResponse(UnitOfMeasure unitOfMeasure) {
        return UnitOfMeasureResponse.builder()
                .id(unitOfMeasure.getId())
                .code(unitOfMeasure.getCode())
                .name(unitOfMeasure.getName())
                .createdAt(unitOfMeasure.getCreatedAt())
                .updatedAt(unitOfMeasure.getUpdatedAt())
                .build();
    }
}
