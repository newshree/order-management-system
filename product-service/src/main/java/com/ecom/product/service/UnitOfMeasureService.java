package com.ecom.product.service;

import java.util.List;
import java.util.UUID;

import com.ecom.product.dto.request.UnitOfMeasureCreateRequest;
import com.ecom.product.dto.request.UnitOfMeasureUpdateRequest;
import com.ecom.product.dto.response.UnitOfMeasureResponse;

/**
 * Service interface for UnitOfMeasure operations.
 *
 * Defines business logic for unit of measure management.
 */
public interface UnitOfMeasureService {

    /**
     * Creates a new unit of measure (global master data).
     *
     * @param request the unit of measure creation request
     * @param tenantId the tenant ID (informational, UOM is global)
     * @return the created unit of measure response
     */
    UnitOfMeasureResponse createUnitOfMeasure(UnitOfMeasureCreateRequest request, String tenantId);

    /**
     * Retrieves all units of measure (global master data).
     *
     * @param tenantId the tenant ID (informational, UOM is global)
     * @return list of units of measure
     */
    List<UnitOfMeasureResponse> getAllUnitOfMeasures(String tenantId);

    /**
     * Retrieves a unit of measure by ID (global master data).
     *
     * @param id the unit of measure ID
     * @param tenantId the tenant ID (informational, UOM is global)
     * @return the unit of measure response
     */
    UnitOfMeasureResponse getUnitOfMeasureById(UUID id, String tenantId);

    /**
     * Retrieves a unit of measure by code (global master data).
     *
     * @param code the unit of measure code
     * @param tenantId the tenant ID (informational, UOM is global)
     * @return the unit of measure response
     */
    UnitOfMeasureResponse getUnitOfMeasureByCode(String code, String tenantId);

    /**
     * Updates an existing unit of measure (global master data).
     *
     * @param id the unit of measure ID
     * @param request the unit of measure update request
     * @param tenantId the tenant ID (informational, UOM is global)
     * @return the updated unit of measure response
     */
    UnitOfMeasureResponse updateUnitOfMeasure(UUID id, UnitOfMeasureUpdateRequest request, String tenantId);

    /**
     * Deletes a unit of measure by ID (global master data).
     *
     * @param id the unit of measure ID
     * @param tenantId the tenant ID (informational, UOM is global)
     */
    void deleteUnitOfMeasure(UUID id, String tenantId);
}
