package com.ecom.inventory.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.inventory.dto.response.InventoryResponse;
import com.ecom.inventory.dto.response.PageResponse;
import com.ecom.inventory.dto.response.TransactionResponse;
import com.ecom.inventory.entity.Inventory;
import com.ecom.inventory.entity.InventoryTransaction;
import com.ecom.inventory.enums.ErrorCode;
import com.ecom.inventory.enums.TransactionType;
import com.ecom.inventory.exception.BadRequestException;
import com.ecom.inventory.exception.ResourceNotFoundException;
import com.ecom.inventory.mapper.InventoryMapper;
import com.ecom.inventory.repository.InventoryRepository;
import com.ecom.inventory.repository.InventoryTransactionRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for admin inventory operations.
 * Handles inventory creation, bulk updates, and transaction history retrieval.
 */
@Service
@RequiredArgsConstructor
public class InventoryAdminService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final InventoryMapper inventoryMapper;

    /**
     * Create or get inventory for a product.
     * If inventory exists, return it; otherwise create with initial stock.
     *
     * @param tenantId tenant identifier
     * @param productId product identifier
     * @param initialQuantity initial stock quantity
     * @return inventory response
     */
    @Transactional
    public InventoryResponse createOrUpdateInventory(String tenantId, String productId, Integer initialQuantity) {
        if (initialQuantity < 0) {
            throw new BadRequestException(
                    ErrorCode.VALIDATION_FAILED,
                    "Initial quantity cannot be negative"
            );
        }

        Inventory inventory = inventoryRepository.findByTenantIdAndProductId(tenantId, productId)
                .orElseGet(() -> Inventory.builder()
                        .tenantId(tenantId)
                        .productId(productId)
                        .availableQuantity(initialQuantity)
                        .reservedQuantity(0)
                        .build());

        inventory.setAvailableQuantity(initialQuantity);
        inventory = inventoryRepository.save(inventory);

        // Record transaction
        InventoryTransaction transaction = InventoryTransaction.builder()
                .tenantId(tenantId)
                .productId(productId)
                .inventoryId(inventory.getId())
                .quantity(initialQuantity)
                .transactionType(TransactionType.CREATE)
                .build();
        transactionRepository.save(transaction);

        return inventoryMapper.mapToInventoryResponse(inventory);
    }

    /**
     * Update stock for a product (admin operation).
     * Adds quantity to available stock.
     *
     * @param tenantId tenant identifier
     * @param productId product identifier
     * @param quantityToAdd quantity to add
     * @return inventory response
     */
    @Transactional
    public InventoryResponse updateStock(String tenantId, String productId, Integer quantityToAdd) {
        if (quantityToAdd < 0) {
            throw new BadRequestException(
                    ErrorCode.VALIDATION_FAILED,
                    "Quantity cannot be negative"
            );
        }

        Inventory inventory = inventoryRepository.findByTenantIdAndProductId(tenantId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.INVENTORY_NOT_FOUND,
                        "Inventory not found for product: " + productId
                ));

        // Atomically add stock
        int updateCount = inventoryRepository.addStock(tenantId, productId, quantityToAdd);
        if (updateCount == 0) {
            throw new BadRequestException(
                    ErrorCode.CONCURRENT_UPDATE_FAILED,
                    "Failed to update stock: concurrent update detected"
            );
        }

        // Record transaction
        InventoryTransaction transaction = InventoryTransaction.builder()
                .tenantId(tenantId)
                .productId(productId)
                .inventoryId(inventory.getId())
                .quantity(quantityToAdd)
                .transactionType(TransactionType.UPDATE)
                .build();
        transactionRepository.save(transaction);

        // Refresh inventory
        inventory = inventoryRepository.findByTenantIdAndProductId(tenantId, productId).get();
        return inventoryMapper.mapToInventoryResponse(inventory);
    }

    /**
     * Get all inventories with low stock below threshold.
     *
     * @param tenantId tenant identifier
     * @param threshold stock threshold
     * @return list of low stock inventories
     */
    public List<InventoryResponse> getLowStockInventories(String tenantId, Integer threshold) {
        if (threshold < 0) {
            throw new BadRequestException(
                    ErrorCode.VALIDATION_FAILED,
                    "Threshold cannot be negative"
            );
        }

        // Fetch all inventories and filter by threshold
        // In production, consider adding a database view or custom query for better performance
        return inventoryRepository.findAll().stream()
                .filter(inv -> inv.getTenantId().equals(tenantId))
                .filter(inv -> inv.getAvailableQuantity() < threshold)
                .map(inventoryMapper::mapToInventoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all inventories with pagination and sorting.
     *
     * @param tenantId tenant identifier
     * @param sortBy field to sort by (availableQuantity, reservedQuantity, productId)
     * @param sortOrder sort order (ASC, DESC)
     * @param page page number (0-based)
     * @param size page size
     * @return page response with inventories
     */
    public PageResponse<InventoryResponse> getAllInventories(
            String tenantId,
            String sortBy,
            String sortOrder,
            Integer page,
            Integer size
    ) {
        if (page < 0 || size <= 0) {
            throw new BadRequestException(
                    ErrorCode.VALIDATION_FAILED,
                    "Invalid pagination parameters"
            );
        }

        Sort.Direction direction = Sort.Direction.fromString(sortOrder.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Inventory> inventoryPage = inventoryRepository.findAll(pageable);
        // Filter by tenant ID (application-level filtering for now)
        List<InventoryResponse> content = inventoryPage.getContent().stream()
                .filter(inv -> inv.getTenantId().equals(tenantId))
                .map(inventoryMapper::mapToInventoryResponse)
                .collect(Collectors.toList());

        return PageResponse.<InventoryResponse>builder()
                .content(content)
                .pageable(PageResponse.PageableInfo.builder()
                        .pageNumber(page)
                        .pageSize(size)
                        .build())
                .totalElements(inventoryPage.getTotalElements())
                .totalPages(inventoryPage.getTotalPages())
                .first(inventoryPage.isFirst())
                .last(inventoryPage.isLast())
                .build();
    }

    /**
     * Get transaction history for a product.
     *
     * @param tenantId tenant identifier
     * @param productId product identifier
     * @param page page number (0-based)
     * @param size page size
     * @return page response with transactions
     */
    public PageResponse<TransactionResponse> getTransactionHistory(
            String tenantId,
            String productId,
            Integer page,
            Integer size
    ) {
        if (page < 0 || size <= 0) {
            throw new BadRequestException(
                    ErrorCode.VALIDATION_FAILED,
                    "Invalid pagination parameters"
            );
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<InventoryTransaction> transactionPage = transactionRepository.findByTenantIdAndProductId(
                tenantId, productId, pageable
        );

        List<TransactionResponse> content = transactionPage.getContent().stream()
                .map(inventoryMapper::mapToTransactionResponse)
                .collect(Collectors.toList());

        return PageResponse.<TransactionResponse>builder()
                .content(content)
                .pageable(PageResponse.PageableInfo.builder()
                        .pageNumber(page)
                        .pageSize(size)
                        .build())
                .totalElements(transactionPage.getTotalElements())
                .totalPages(transactionPage.getTotalPages())
                .first(transactionPage.isFirst())
                .last(transactionPage.isLast())
                .build();
    }
}

