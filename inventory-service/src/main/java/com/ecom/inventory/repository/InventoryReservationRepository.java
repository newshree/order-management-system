package com.ecom.inventory.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.inventory.entity.InventoryReservation;
import com.ecom.inventory.enums.ReservationStatus;

/**
 * Repository for InventoryReservation entity.
 * Provides CRUD operations with unique constraint protection for idempotency.
 */
@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, UUID> {

    /**
     * Find reservation by order ID, inventory ID, and product ID.
     * Used to check for duplicate reservations (idempotency protection).
     *
     * @param orderId order identifier
     * @param inventoryId inventory identifier
     * @param productId product identifier
     * @return reservation if found
     */
    Optional<InventoryReservation> findByOrderIdAndInventoryIdAndProductId(
            String orderId,
            UUID inventoryId,
            String productId
    );

    /**
     * Find all reservations for an order.
     *
     * @param tenantId tenant identifier
     * @param orderId order identifier
     * @return list of reservations
     */
    List<InventoryReservation> findByTenantIdAndOrderId(String tenantId, String orderId);

    /**
     * Find reservation by order ID and product ID.
     *
     * @param orderId order identifier
     * @param productId product identifier
     * @return reservation if found
     */
    Optional<InventoryReservation> findByOrderIdAndProductId(String orderId, String productId);

    /**
     * Find all reservations with a specific status.
     *
     * @param tenantId tenant identifier
     * @param status reservation status
     * @return list of reservations
     */
    List<InventoryReservation> findByTenantIdAndStatus(String tenantId, ReservationStatus status);
}

