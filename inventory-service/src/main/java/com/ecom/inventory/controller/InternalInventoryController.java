package com.ecom.inventory.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.inventory.dto.request.InventoryStockRequest;
import com.ecom.inventory.dto.request.StockUpdateRequest;
import com.ecom.inventory.dto.response.ApiResponse;
import com.ecom.inventory.dto.response.InventoryResponse;
import com.ecom.inventory.dto.response.ReservationResponse;
import com.ecom.inventory.service.InventoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Internal API Controller for Inventory Microservice.
 * Used by Order Service for stock operations.
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InternalInventoryController {

    private final InventoryService inventoryService;

    private static final String TENANT_ID = "2f34e34a-a524-4aff-9746-e9cfcb089b66";

    /**
     * Get inventory details for a product.
     *
     * @param productId product identifier
     * @return inventory details with available and reserved quantities
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventory(
            @PathVariable String productId,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId
    ) {
        InventoryResponse inventory = inventoryService.getInventory(tenantId, productId);

        return ResponseEntity.ok(ApiResponse.<InventoryResponse>builder()
                .success(true)
                .data(inventory)
                .message("Inventory retrieved successfully")
                .build());
    }

    /**
     * Add stock for a product.
     *
     * @param request stock update request containing productId and quantity
     * @return updated inventory details
     */
    @PostMapping("/addStock")
    public ResponseEntity<ApiResponse<InventoryResponse>> addStock(
            @Valid @RequestBody StockUpdateRequest request,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId
    ) {
        InventoryResponse inventory = inventoryService.addStock(tenantId, request.getProductId(), request.getQuantity());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<InventoryResponse>builder()
                .success(true)
                .data(inventory)
                .message("Stock added successfully")
                .build());
    }

    /**
     * Reserve stock for an order.
     *
     * @param request reserve stock request
     * @return reservation details
     */
    @PostMapping("/reserveStock")
    public ResponseEntity<ApiResponse<ReservationResponse>> reserveStock(
            @Valid @RequestBody InventoryStockRequest request,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId
    ) {
        ReservationResponse reservation = inventoryService.reserveStock(
                tenantId,
                request.getProductId(),
                request.getOrderId(),
                request.getQuantity()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ReservationResponse>builder()
                .success(true)
                .data(reservation)
                .message("Stock reserved successfully")
                .build());
    }

    /**
     * Commit reserved stock (order paid).
     * Validates that commit matches reserved quantity and order ID.
     *
     * @param request commit stock request
     * @return updated reservation details
     */
    @PostMapping("/commitStock")
    public ResponseEntity<ApiResponse<ReservationResponse>> commitStock(
            @Valid @RequestBody InventoryStockRequest request,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId
    ) {
        ReservationResponse reservation = inventoryService.commitStock(
                tenantId,
                request.getProductId(),
                request.getOrderId(),
                request.getQuantity()
        );
        return ResponseEntity.ok(ApiResponse.<ReservationResponse>builder()
                .success(true)
                .data(reservation)
                .message("Stock committed successfully")
                .build());
    }

    /**
     * Release reserved stock (order cancelled/failed).
     * Validates that release matches reserved quantity and order ID.
     *
     * @param request release stock request
     * @return updated reservation details
     */
    @PostMapping("/releaseStock")
    public ResponseEntity<ApiResponse<ReservationResponse>> releaseStock(
            @Valid @RequestBody InventoryStockRequest request,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId
    ) {
        ReservationResponse reservation = inventoryService.releaseStock(
                tenantId,
                request.getProductId(),
                request.getOrderId(),
                request.getQuantity()
        );
        return ResponseEntity.ok(ApiResponse.<ReservationResponse>builder()
                .success(true)
                .data(reservation)
                .message("Stock released successfully")
                .build());
    }
}

