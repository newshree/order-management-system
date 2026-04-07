package com.ecom.product.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.product.dto.request.UnitOfMeasureCreateRequest;
import com.ecom.product.dto.request.UnitOfMeasureUpdateRequest;
import com.ecom.product.dto.response.UnitOfMeasureResponse;
import com.ecom.product.entity.UnitOfMeasure;
import com.ecom.product.enums.ErrorCode;
import com.ecom.product.exception.BadRequestException;
import com.ecom.product.exception.ResourceNotFoundException;
import com.ecom.product.mapper.UnitOfMeasureMapper;
import com.ecom.product.repository.UnitOfMeasureRepository;
import com.ecom.product.service.UnitOfMeasureService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of UnitOfMeasureService.
 *
 * Provides business logic for unit of measure management operations.
 */
@Service
@RequiredArgsConstructor
public class UnitOfMeasureServiceImpl implements UnitOfMeasureService {

    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final UnitOfMeasureMapper unitOfMeasureMapper;

    /**
     * Creates a new unit of measure (global master data).
     *
     * @param request the unit of measure creation request
     * @param tenantId the tenant ID (informational, UOM is global)
     * @return the created unit of measure response
     * @throws BadRequestException if unit of measure code already exists
     */
    @Override
    @Transactional
    public UnitOfMeasureResponse createUnitOfMeasure(UnitOfMeasureCreateRequest request, String tenantId) {
        // Check if unit of measure code already exists
        if (unitOfMeasureRepository.findByCode(request.getCode()).isPresent()) {
            throw new BadRequestException(ErrorCode.DUPLICATE_UNIT_OF_MEASURE,
                    "Unit of measure with code '" + request.getCode() + "' already exists");
        }

        UnitOfMeasure unitOfMeasure = unitOfMeasureMapper.mapToEntity(request);
        UnitOfMeasure savedUnitOfMeasure = unitOfMeasureRepository.save(unitOfMeasure);
        return unitOfMeasureMapper.mapToResponse(savedUnitOfMeasure);
    }

    /**
     * Retrieves all units of measure (global master data).
     *
     * @param tenantId the tenant ID (informational, UOM is global)
     * @return list of unit of measure responses
     */
    @Override
    @Transactional(readOnly = true)
    public List<UnitOfMeasureResponse> getAllUnitOfMeasures(String tenantId) {
        return unitOfMeasureRepository.findAll()
                .stream()
                .map(unitOfMeasureMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a unit of measure by ID (global master data).
     *
     * @param id the unit of measure ID
     * @param tenantId the tenant ID (informational, UOM is global)
     * @return the unit of measure response
     * @throws ResourceNotFoundException if unit of measure not found
     */
    @Override
    @Transactional(readOnly = true)
    public UnitOfMeasureResponse getUnitOfMeasureById(UUID id, String tenantId) {
        UnitOfMeasure unitOfMeasure = findUnitOfMeasure(id); // Ensure unit of measure exists
        return unitOfMeasureMapper.mapToResponse(unitOfMeasure);
    }

    /**
     * Retrieves a unit of measure by code (global master data).
     *
     * @param code the unit of measure code
     * @param tenantId the tenant ID (informational, UOM is global)
     * @return the unit of measure response
     * @throws ResourceNotFoundException if unit of measure not found
     */
    @Override
    @Transactional(readOnly = true)
    public UnitOfMeasureResponse getUnitOfMeasureByCode(String code, String tenantId) {
        UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.UNIT_OF_MEASURE_NOT_FOUND,
                        "Unit of measure not found with code: " + code));

        return unitOfMeasureMapper.mapToResponse(unitOfMeasure);
    }

    /**
     * Updates an existing unit of measure (global master data).
     *
     * @param id the unit of measure ID
     * @param request the unit of measure update request
     * @param tenantId the tenant ID (informational, UOM is global)
     * @return the updated unit of measure response
     * @throws ResourceNotFoundException if unit of measure not found
     */
    @Override
    @Transactional
    public UnitOfMeasureResponse updateUnitOfMeasure(UUID id, UnitOfMeasureUpdateRequest request, String tenantId) {
        UnitOfMeasure unitOfMeasure = findUnitOfMeasure(id); // Ensure unit of measure exists

        UnitOfMeasure updatedUnitOfMeasure = unitOfMeasureMapper.mapToEntity(request, unitOfMeasure);
        UnitOfMeasure savedUnitOfMeasure = unitOfMeasureRepository.save(updatedUnitOfMeasure);
        return unitOfMeasureMapper.mapToResponse(savedUnitOfMeasure);
    }

    /**
     * Deletes a unit of measure by ID (global master data).
     *
     * @param id the unit of measure ID
     * @param tenantId the tenant ID (informational, UOM is global)
     * @throws ResourceNotFoundException if unit of measure not found
     */
    @Override
    @Transactional
    public void deleteUnitOfMeasure(UUID id, String tenantId) {
        UnitOfMeasure unitOfMeasure = findUnitOfMeasure(id); // Ensure unit of measure exists
        unitOfMeasureRepository.delete(unitOfMeasure);
    }

    /**
     * Helper method to find a unit of measure by ID and throw ResourceNotFoundException if not found.
     * 
     * @param id the unit of measure ID
     * @return the found unit of measure
     * @throws ResourceNotFoundException if unit of measure not found
     */
    private UnitOfMeasure findUnitOfMeasure(UUID id) {
        return unitOfMeasureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.UNIT_OF_MEASURE_NOT_FOUND,
                        "Unit of measure not found with id: " + id));
    }
}
