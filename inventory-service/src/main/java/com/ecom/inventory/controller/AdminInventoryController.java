package com.ecom.inventory.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.inventory.dto.request.BulkStockUpdateRequest;
import com.ecom.inventory.dto.response.ApiResponse;
import com.ecom.inventory.dto.response.InventoryResponse;
import com.ecom.inventory.dto.response.PageResponse;
import com.ecom.inventory.dto.response.TransactionResponse;
import com.ecom.inventory.service.InventoryAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Admin API Controller for Inventory Microservice.
 *
 * Administrative operations for inventory management.
 * Includes endpoints for monitoring low stock, managing inventory, and viewing transaction history.
 */
@RestController
@RequestMapping("/api/admin/inventory")
@RequiredArgsConstructor
public class AdminInventoryController {

    private final InventoryAdminService adminService;
    private static final String TENANT_ID = "2f34e34a-a524-4aff-9746-e9cfcb089b66";

    /**
     * Get inventories with low stock below threshold.
     *
     * @param threshold stock threshold value
     * @return list of low stock inventories
     */
    @GetMapping("/getlowStock/{threshold}")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getLowStockInventories(
            @PathVariable Integer threshold,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId
    ) {
        List<InventoryResponse> inventories = adminService.getLowStockInventories(tenantId, threshold);
        return ResponseEntity.ok(ApiResponse.<List<InventoryResponse>>builder()
                .success(true)
                .data(inventories)
                .message("Retrieved " + inventories.size() + " low stock inventories")
                .build());
    }

    /**
     * Get all inventories with pagination and sorting.
     * GET /api/admin/inventory?sortBy=availableQuantity&sortOrder=DESC&page=0&size=10
     *
     * @param sortBy field to sort by (availableQuantity, reservedQuantity, productId)
     * @param sortOrder sort order (ASC, DESC)
     * @param page page number (0-based)
     * @param size page size
     * @return paginated inventory list
     */
    @GetMapping("/getAllInventories")
    public ResponseEntity<ApiResponse<PageResponse<InventoryResponse>>> getAllInventories(
            @RequestParam(defaultValue = "productId") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortOrder,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId
    ) {
        PageResponse<InventoryResponse> pageResponse = adminService.getAllInventories(
                tenantId, sortBy, sortOrder, page, size
        );
        return ResponseEntity.ok(ApiResponse.<PageResponse<InventoryResponse>>builder()
                .success(true)
                .data(pageResponse)
                .message("Retrieved " + pageResponse.getContent().size() + " inventories")
                .build());
    }

    /**
     * Create or update inventory for a product.
     * POST /api/admin/inventory?productId=PROD001&quantity=100
     *
     * @param productId product identifier
     * @param quantity initial or update quantity
     * @return created/updated inventory details
     */
    @PostMapping("/createOrUpdate")
    public ResponseEntity<ApiResponse<InventoryResponse>> createOrUpdateInventory(
            @RequestParam String productId,
            @RequestParam Integer quantity,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId
    ) {
        InventoryResponse inventory = adminService.createOrUpdateInventory(tenantId, productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<InventoryResponse>builder()
                .success(true)
                .data(inventory)
                .message("Inventory created/updated successfully")
                .build());
    }

    /**
     * Bulk update stock for multiple products.
     * POST /api/admin/inventory/bulkStock
     *
     * Request body:
     * {
     *   "stockUpdates": [
     *     { "productId": "PROD001", "quantity": 50 },
     *     { "productId": "PROD002", "quantity": 30 }
     *   ]
     * }
     *
     * @param request bulk stock update request
     * @return list of updated inventories
     */
    @PostMapping("/bulkStockUpdate")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> bulkUpdateStock(
            @Valid @RequestBody BulkStockUpdateRequest request,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId
    ) {
        List<InventoryResponse> results = new ArrayList<>();
        for (var stockUpdate : request.getStockUpdates()) {
            InventoryResponse inventory = adminService.updateStock(
                    tenantId,
                    stockUpdate.getProductId(),
                    stockUpdate.getQuantity()
            );
            results.add(inventory);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<List<InventoryResponse>>builder()
                .success(true)
                .data(results)
                .message("Bulk stock update completed for " + results.size() + " products")
                .build());
    }

    /**
     * Get transaction history for a product.
     * GET /api/admin/inventory/transactions?productId=PROD001&page=0&size=10
     *
     * @param productId product identifier
     * @param page page number (0-based)
     * @param size page size
     * @return paginated transaction history
     */
    @GetMapping("/getTransactionHistory")
    public ResponseEntity<ApiResponse<PageResponse<TransactionResponse>>> getTransactionHistory(
            @RequestParam String productId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = TENANT_ID) String tenantId
    ) {
        PageResponse<TransactionResponse> pageResponse = adminService.getTransactionHistory(
                tenantId, productId, page, size
        );
        return ResponseEntity.ok(ApiResponse.<PageResponse<TransactionResponse>>builder()
                .success(true)
                .data(pageResponse)
                .message("Retrieved " + pageResponse.getContent().size() + " transactions")
                .build());
    }
}

