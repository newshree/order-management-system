package com.ecom.inventory.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.inventory.dto.response.InventoryResponse;
import com.ecom.inventory.dto.response.ReservationResponse;
import com.ecom.inventory.entity.Inventory;
import com.ecom.inventory.entity.InventoryReservation;
import com.ecom.inventory.entity.InventoryTransaction;
import com.ecom.inventory.enums.ErrorCode;
import com.ecom.inventory.enums.ReservationStatus;
import com.ecom.inventory.enums.TransactionType;
import com.ecom.inventory.exception.BadRequestException;
import com.ecom.inventory.exception.ResourceNotFoundException;
import com.ecom.inventory.mapper.InventoryMapper;
import com.ecom.inventory.repository.InventoryRepository;
import com.ecom.inventory.repository.InventoryReservationRepository;
import com.ecom.inventory.repository.InventoryTransactionRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

/**
 * Service for managing inventory operations.
 * Handles stock reservation, commitment, release, and transaction recording.
 * Implements race condition and idempotency protection.
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    private final InventoryReservationRepository reservationRepository;

    private final InventoryTransactionRepository transactionRepository;

    private final InventoryMapper inventoryMapper;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Get inventory details by product ID.
     *
     * @param tenantId tenant identifier
     * @param productId product identifier
     * @return inventory response
     */
    public InventoryResponse getInventory(String tenantId, String productId) {

        Inventory inventory = fetchInventoryByProductId(tenantId, productId);

        return inventoryMapper.mapToInventoryResponse(inventory);
    }

    /**
     * Add stock quantity to inventory.
     * If product exists, updates the available stock.
     * If product doesn't exist, creates new inventory with the given quantity.
     *
     * @param tenantId tenant identifier
     * @param productId product identifier
     * @param quantity quantity to add
     * @return updated inventory response
     */
    @Transactional
    public InventoryResponse addStock(String tenantId, String productId, Integer quantity) {

        // Check if inventory exists for the product
        var existingInventory = inventoryRepository.findByTenantIdAndProductId(tenantId, productId);

        if (existingInventory.isPresent()) {

            // Product exists - update available stock
            int updateCount = inventoryRepository.addStock(tenantId, productId, quantity);

            if (updateCount == 0) {
                throw new BadRequestException(
                        ErrorCode.CONCURRENT_UPDATE_FAILED,
                        "Failed to add stock: concurrent update detected"
                );
            }
            Inventory inventory = existingInventory.get();

            // Insert a transaction record for audit purposes.
            recordTransaction(tenantId, productId, null, inventory.getId(), quantity,
                    TransactionType.UPDATE);

            // Refresh and return updated inventory
            inventory = inventoryRepository.findByTenantIdAndProductId(tenantId, productId).get();

            return inventoryMapper.mapToInventoryResponse(inventory);

        } else {

            // Product doesn't exist - create new inventory
            Inventory newInventory = Inventory.builder()
                    .tenantId(tenantId)
                    .productId(productId)
                    .availableQuantity(quantity)
                    .reservedQuantity(0)
                    .build();

            newInventory = inventoryRepository.save(newInventory);

            entityManager.flush();
            entityManager.refresh(newInventory);

            // Insert a transaction record for audit purposes.
            recordTransaction(tenantId, productId, null, newInventory.getId(), quantity,
                    TransactionType.CREATE);

            return inventoryMapper.mapToInventoryResponse(newInventory);
        }
    }

    /**
     * Reserve stock for an order.
     * Implements race condition protection with atomic SQL update.
     * Implements idempotency protection with unique constraint check.
     *
     * @param tenantId tenant identifier
     * @param productId product identifier
     * @param orderId order identifier
     * @param quantity quantity to reserve
     * @return reservation response
     */
    @Transactional
    public ReservationResponse reserveStock(String tenantId, String productId, String orderId, Integer quantity) {
        
        // Get inventory and validate it exists
        Inventory inventory = fetchInventoryByProductId(tenantId, productId);

        // Check for duplicate reservation (idempotency protection)
        var existingReservation = reservationRepository.findByOrderIdAndProductId(orderId, productId);

        if (existingReservation.isPresent()) {

            InventoryReservation res = existingReservation.get();

            // Case 1: Already reserved with same quantity, return existing reservation → idempotent success
            if (res.getStatus() == ReservationStatus.RESERVED && res.getQuantity().equals(quantity)) {
                return inventoryMapper.mapToReservationResponse(res);
            }

            // Case 2: Already committed → cannot reserve again
            if (res.getStatus() == ReservationStatus.COMMITTED) {
                throw new BadRequestException(
                        ErrorCode.RESERVATION_ALREADY_COMMITTED,
                        "Stock already committed for this order"
                );
            }

            // Case 3: RESERVED but different quantity → duplicate request
            if (res.getStatus() == ReservationStatus.RESERVED) {
                throw new BadRequestException(
                        ErrorCode.DUPLICATE_RESERVATION,
                        "Stock already reserved for this order"
                );
            }

            /**
             * Handles the case where a reservation already exists but was previously RELEASED.
             * This can happen if an order was cancelled/failed and later retried.
             * Instead of creating a new reservation (which would violate the unique constraint
             * on order_id + product_id), the existing record is reused and moved back to
             * RESERVED state after atomically reserving stock again.
             */
            if (res.getStatus() == ReservationStatus.RELEASED) {

                int updateCount = inventoryRepository.atomicReserveStock(tenantId, productId, quantity);

                if (updateCount == 0) {
                    throw new BadRequestException(
                            ErrorCode.INSUFFICIENT_STOCK,
                            "Insufficient stock available for product: " + productId
                    );
                }

                res.setQuantity(quantity);
                res.setStatus(ReservationStatus.RESERVED);
                res = reservationRepository.save(res);

                entityManager.flush();
                entityManager.refresh(res);

                recordTransaction(
                        tenantId,
                        productId,
                        orderId,
                        inventory.getId(),
                        quantity,
                        TransactionType.RESERVE
                );

                return inventoryMapper.mapToReservationResponse(res);
            }
        }

        // Atomically reserve stock (race condition protection)
        int updateCount = inventoryRepository.atomicReserveStock(tenantId, productId, quantity);

        if (updateCount == 0) {
            throw new BadRequestException(
                    ErrorCode.INSUFFICIENT_STOCK,
                    "Insufficient stock available for product: " + productId
            );
        }

        // Create reservation record
        InventoryReservation reservation = InventoryReservation.builder()
                .tenantId(tenantId)
                .orderId(orderId)
                .inventoryId(inventory.getId())
                .productId(productId)
                .quantity(quantity)
                .status(ReservationStatus.RESERVED)
                .build();

        reservation = reservationRepository.save(reservation);

        entityManager.flush();
        entityManager.refresh(reservation);

        // Record transaction
        recordTransaction(tenantId, productId, orderId, inventory.getId(), quantity,
                TransactionType.RESERVE);

        return inventoryMapper.mapToReservationResponse(reservation);
    }

    /**
     * Commit reserved stock (after payment).
     * Decreases available_quantity and reserved_quantity.
     *
     * @param tenantId tenant identifier
     * @param productId product identifier
     * @param orderId order identifier
     * @param quantity quantity to commit
     * @return reservation response
     */
    @Transactional
    public ReservationResponse commitStock(String tenantId, String productId, String orderId, Integer quantity) {
        // Validate reservation exists and has matching quantity
        InventoryReservation reservation = reservationRepository.findByOrderIdAndProductId(orderId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RESERVATION_NOT_FOUND,
                        "Reservation not found for order: " + orderId + ", product: " + productId
                ));

        // Validate if Reserved and Commit quantity matches
        if (!reservation.getQuantity().equals(quantity)) {
            throw new BadRequestException(
                    ErrorCode.INVALID_RESERVATION_STATUS,
                    "Quantity mismatch: reserved=" + reservation.getQuantity() + ", commit=" + quantity
            );
        }

        // Validate if already committed - idempotent success
        if (reservation.getStatus() == ReservationStatus.COMMITTED) {
            return inventoryMapper.mapToReservationResponse(reservation);
        }

        // Validate reservation is in RESERVED status
        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            throw new BadRequestException(
                    ErrorCode.INVALID_RESERVATION_STATUS,
                    "Reservation is not in RESERVED status: " + reservation.getStatus()
            );
        }

        // Atomically commit stock
        int updateCount = inventoryRepository.atomicCommitStock(tenantId, productId, quantity);
        if (updateCount == 0) {
            throw new BadRequestException(
                    ErrorCode.CONCURRENT_UPDATE_FAILED,
                    "Failed to commit stock: concurrent update or invalid state"
            );
        }

        // Update reservation status
        reservation.setStatus(ReservationStatus.COMMITTED);
        reservation = reservationRepository.save(reservation);

        entityManager.flush();
        entityManager.refresh(reservation);

        // Record transaction
        recordTransaction(tenantId, productId, orderId, reservation.getInventoryId(), quantity,
                TransactionType.COMMIT);

        return inventoryMapper.mapToReservationResponse(reservation);
    }

    /**
     * Release reserved stock (order cancelled/failed).
     * Decreases reserved_quantity.
     *
     * @param tenantId tenant identifier
     * @param productId product identifier
     * @param orderId order identifier
     * @param quantity quantity to release
     * @return reservation response
     */
    @Transactional
    public ReservationResponse releaseStock(String tenantId, String productId, String orderId, Integer quantity) {
        // Validate reservation exists and has matching quantity
        InventoryReservation reservation = reservationRepository.findByOrderIdAndProductId(orderId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RESERVATION_NOT_FOUND,
                        "Reservation not found for order: " + orderId + ", product: " + productId
                ));

        if (!reservation.getQuantity().equals(quantity)) {
            throw new BadRequestException(
                    ErrorCode.INVALID_RESERVATION_STATUS,
                    "Quantity mismatch: reserved=" + reservation.getQuantity() + ", release=" + quantity
            );
        }

        if (reservation.getStatus() == ReservationStatus.COMMITTED) {
            throw new BadRequestException(
                    ErrorCode.INVALID_RESERVATION_STATUS,
                    "Reservation already committed and cannot be released"
            );
        }

        if (reservation.getStatus() == ReservationStatus.RELEASED) {
            throw new BadRequestException(
                    ErrorCode.INVALID_RESERVATION_STATUS,
                    "Reservation is already released"
            );
        }

        // Atomically release stock
        int updateCount = inventoryRepository.atomicReleaseStock(tenantId, productId, quantity);
        if (updateCount == 0) {
            throw new BadRequestException(
                    ErrorCode.CONCURRENT_UPDATE_FAILED,
                    "Failed to release stock: concurrent update or invalid state"
            );
        }

        // Update reservation status
        reservation.setStatus(ReservationStatus.RELEASED);
        reservation = reservationRepository.save(reservation);

        entityManager.flush();
        entityManager.refresh(reservation);

        // Record transaction
        recordTransaction(tenantId, productId, orderId, reservation.getInventoryId(), quantity,
                TransactionType.RELEASE);

        return inventoryMapper.mapToReservationResponse(reservation);
    }

    /**
     * Insert a transaction record for audit purposes.
     *
     * @param tenantId tenant identifier
     * @param productId product identifier
     * @param orderId order identifier
     * @param inventoryId inventory identifier
     * @param quantity transaction quantity
     * @param transactionType type of transaction
     */
    private void recordTransaction(String tenantId, String productId, String orderId, UUID inventoryId,
                                   Integer quantity, TransactionType transactionType) {
        InventoryTransaction transaction = InventoryTransaction.builder()
                .tenantId(tenantId)
                .productId(productId)
                .orderId(orderId)
                .inventoryId(inventoryId)
                .quantity(quantity)
                .transactionType(transactionType)
                .build();
        transactionRepository.save(transaction);
    }
    
    /**
     * Fetch inventory by product ID with tenant context.
     * 
     * @param tenantId tenant identifier
     * @param productId product identifier
     * @return inventory
     */
    private Inventory fetchInventoryByProductId(String tenantId, String productId) {
        return inventoryRepository.findByTenantIdAndProductId(tenantId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.INVENTORY_NOT_FOUND,
                        "Inventory not found for product: " + productId
                ));
    }
}

