package com.ecom.inventory.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.inventory.entity.Inventory;

/**
 * Repository for Inventory entity.
 * Provides CRUD operations and custom atomic stock update queries.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    /**
     * Find inventory by tenant and product ID.
     *
     * @param tenantId tenant identifier
     * @param productId product identifier
     * @return inventory if found
     */
    Optional<Inventory> findByTenantIdAndProductId(String tenantId, String productId);

    /**
     * Atomically reserve stock with race condition protection.
     * Updates reserved_quantity only if sufficient available stock exists.
     *
     * @param tenantId tenant identifier
     * @param productId product identifier
     * @param quantity quantity to reserve
     * @return number of rows updated (0 if insufficient stock, 1 if successful)
     */
    @Modifying
    @Query(value = """
        UPDATE inventories
        SET reserved_quantity = reserved_quantity + :quantity
        WHERE tenant_id = :tenantId
        AND product_id = :productId
        AND available_quantity - reserved_quantity >= :quantity
        """, nativeQuery = true)
    int atomicReserveStock(
            @Param("tenantId") String tenantId,
            @Param("productId") String productId,
            @Param("quantity") Integer quantity
    );

    /**
     * Atomically commit reserved stock.
     * Decreases available_quantity and reserved_quantity.
     *
     * @param tenantId tenant identifier
     * @param productId product identifier
     * @param quantity quantity to commit
     * @return number of rows updated
     */
    @Modifying
    @Query(value = """
        UPDATE inventories
        SET available_quantity = available_quantity - :quantity,
            reserved_quantity = reserved_quantity - :quantity
        WHERE tenant_id = :tenantId
        AND product_id = :productId
        AND reserved_quantity >= :quantity
        """, nativeQuery = true)
    int atomicCommitStock(
            @Param("tenantId") String tenantId,
            @Param("productId") String productId,
            @Param("quantity") Integer quantity
    );

    /**
     * Atomically release reserved stock.
     * Decreases reserved_quantity.
     *
     * @param tenantId tenant identifier
     * @param productId product identifier
     * @param quantity quantity to release
     * @return number of rows updated
     */
    @Modifying
    @Query(value = """
        UPDATE inventories
        SET reserved_quantity = reserved_quantity - :quantity
        WHERE tenant_id = :tenantId
        AND product_id = :productId
        AND reserved_quantity >= :quantity
        """, nativeQuery = true)
    int atomicReleaseStock(
            @Param("tenantId") String tenantId,
            @Param("productId") String productId,
            @Param("quantity") Integer quantity
    );

    /**
     * Add new stock to inventory.
     * Increases available_quantity.
     *
     * @param tenantId tenant identifier
     * @param productId product identifier
     * @param quantity quantity to add
     * @return number of rows updated
     */
    @Modifying(clearAutomatically = true)
    @Query(value = """
        UPDATE inventories
        SET available_quantity = available_quantity + :quantity
        WHERE tenant_id = :tenantId
        AND product_id = :productId
        """, nativeQuery = true)
    int addStock(
            @Param("tenantId") String tenantId,
            @Param("productId") String productId,
            @Param("quantity") Integer quantity
    );
}

