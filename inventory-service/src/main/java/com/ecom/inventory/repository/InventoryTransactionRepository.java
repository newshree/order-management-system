package com.ecom.inventory.repository;

import com.ecom.inventory.entity.InventoryTransaction;
import com.ecom.inventory.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for InventoryTransaction entity.
 * Provides CRUD operations and query methods for transaction history.
 */
@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, UUID> {

    /**
     * Find all transactions for a product.
     *
     * @param tenantId tenant identifier
     * @param productId product identifier
     * @param pageable pagination details
     * @return page of transactions
     */
    Page<InventoryTransaction> findByTenantIdAndProductId(String tenantId, String productId, Pageable pageable);

    /**
     * Find all transactions for an order.
     *
     * @param tenantId tenant identifier
     * @param orderId order identifier
     * @return list of transactions
     */
    List<InventoryTransaction> findByTenantIdAndOrderId(String tenantId, String orderId);

    /**
     * Find all transactions with a specific type.
     *
     * @param tenantId tenant identifier
     * @param transactionType transaction type
     * @param pageable pagination details
     * @return page of transactions
     */
    Page<InventoryTransaction> findByTenantIdAndTransactionType(
            String tenantId,
            TransactionType transactionType,
            Pageable pageable
    );

    /**
     * Find all transactions for a tenant.
     *
     * @param tenantId tenant identifier
     * @param pageable pagination details
     * @return page of transactions
     */
    Page<InventoryTransaction> findByTenantId(String tenantId, Pageable pageable);
}

