package com.ecom.inventory.mapper;

import com.ecom.inventory.dto.response.InventoryResponse;
import com.ecom.inventory.dto.response.ReservationResponse;
import com.ecom.inventory.dto.response.TransactionResponse;
import com.ecom.inventory.entity.Inventory;
import com.ecom.inventory.entity.InventoryReservation;
import com.ecom.inventory.entity.InventoryTransaction;

/**
 * Mapper interface for converting between domain models and DTOs.
 *
 * Follows the Mapper/Converter design pattern to maintain separation of concerns.
 * This interface ensures that domain entities remain independent of presentation layer concerns.
 *
 * Principle Applied: Single Responsibility Principle (SRP)
 * - Mapper focuses solely on data transformation
 */
public interface InventoryMapper {

    InventoryResponse mapToInventoryResponse(Inventory inventory);

    ReservationResponse mapToReservationResponse(InventoryReservation reservation);

    TransactionResponse mapToTransactionResponse(InventoryTransaction transaction);

}
