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

import com.ecom.product.dto.request.UnitOfMeasureCreateRequest;
import com.ecom.product.dto.request.UnitOfMeasureUpdateRequest;
import com.ecom.product.dto.response.ApiResponse;
import com.ecom.product.dto.response.UnitOfMeasureResponse;
import com.ecom.product.service.UnitOfMeasureService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Unit of Measure management.
 *
 * Provides endpoints for CRUD operations on units of measure.
 */
@RestController
@RequestMapping("/api/units-of-measure")
@RequiredArgsConstructor
public class UnitOfMeasureController {

    private final UnitOfMeasureService unitOfMeasureService;
    private static final String TENANT_ID = "2f34e34a-a524-4aff-9702-35e6f8c6f9d1";

    /**
     * Creates a new unit of measure.
     *
     * @param request the unit of measure creation request
     * @param tenantId the tenant ID from request header (optional for global UOM)
     * @return API response with the created unit of measure
     */
    @PostMapping("/createUnitOfMeasure")
    public ResponseEntity<ApiResponse<UnitOfMeasureResponse>> createUnitOfMeasure(
            @Valid @RequestBody UnitOfMeasureCreateRequest request,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        UnitOfMeasureResponse unitOfMeasure = unitOfMeasureService.createUnitOfMeasure(request, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<UnitOfMeasureResponse>builder()
                .success(true)
                .data(unitOfMeasure)
                .message("Unit of measure created successfully")
                .build());
    }

    /**
     * Retrieves all units of measure.
     *
     * @param tenantId the tenant ID from request header (optional for global UOM)
     * @return API response with list of units of measure
     */
    @GetMapping("/getAllUnitOfMeasures")
    public ResponseEntity<ApiResponse<List<UnitOfMeasureResponse>>> getAllUnitOfMeasures(
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        List<UnitOfMeasureResponse> unitOfMeasures = unitOfMeasureService.getAllUnitOfMeasures(tenantId);
        return ResponseEntity.ok(ApiResponse.<List<UnitOfMeasureResponse>>builder()
                .success(true)
                .data(unitOfMeasures)
                .message("Units of measure retrieved successfully")
                .build());
    }

    /**
     * Retrieves a unit of measure by ID.
     *
     * @param id the unit of measure ID
     * @param tenantId the tenant ID from request header (optional for global UOM)
     * @return API response with the unit of measure
     */
    @GetMapping("/getUnitOfMeasureById/{id}")
    public ResponseEntity<ApiResponse<UnitOfMeasureResponse>> getUnitOfMeasureById(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        UnitOfMeasureResponse unitOfMeasure = unitOfMeasureService.getUnitOfMeasureById(id, tenantId);
        return ResponseEntity.ok(ApiResponse.<UnitOfMeasureResponse>builder()
                .success(true)
                .data(unitOfMeasure)
                .message("Unit of measure retrieved successfully")
                .build());
    }

    /**
     * Retrieves a unit of measure by code.
     *
     * @param code the unit of measure code
     * @param tenantId the tenant ID from request header (optional for global UOM)
     * @return API response with the unit of measure
     */
    @GetMapping("/getUnitOfMeasureByCode/{code}")
    public ResponseEntity<ApiResponse<UnitOfMeasureResponse>> getUnitOfMeasureByCode(
            @PathVariable String code,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        UnitOfMeasureResponse unitOfMeasure = unitOfMeasureService.getUnitOfMeasureByCode(code, tenantId);
        return ResponseEntity.ok(ApiResponse.<UnitOfMeasureResponse>builder()
                .success(true)
                .data(unitOfMeasure)
                .message("Unit of measure retrieved successfully")
                .build());
    }
    
    /**
     * Updates an existing unit of measure.
     *
     * @param id the unit of measure ID
     * @param request the unit of measure update request
     * @param tenantId the tenant ID from request header (optional for global UOM)
     * @return API response with the updated unit of measure
     */
    @PutMapping("/updateUnitOfMeasure/{id}")
    public ResponseEntity<ApiResponse<UnitOfMeasureResponse>> updateUnitOfMeasure(
            @PathVariable UUID id,
            @Valid @RequestBody UnitOfMeasureUpdateRequest request,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        UnitOfMeasureResponse unitOfMeasure = unitOfMeasureService.updateUnitOfMeasure(id, request, tenantId);
        return ResponseEntity.ok(ApiResponse.<UnitOfMeasureResponse>builder()
                .success(true)
                .data(unitOfMeasure)
                .message("Unit of measure updated successfully")
                .build());
    }

    /**
     * Deletes a unit of measure.
     *
     * @param id the unit of measure ID
     * @param tenantId the tenant ID from request header (optional for global UOM)
     * @return API response indicating successful deletion
     */
    @DeleteMapping("/deleteUnitOfMeasure/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUnitOfMeasure(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId) {
        unitOfMeasureService.deleteUnitOfMeasure(id, tenantId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Unit of measure deleted successfully")
                .build());
    }
}
