package com.ecom.inventory.mapper;

import org.springframework.stereotype.Component;

import com.ecom.inventory.dto.response.InventoryResponse;
import com.ecom.inventory.dto.response.ReservationResponse;
import com.ecom.inventory.dto.response.TransactionResponse;
import com.ecom.inventory.entity.Inventory;
import com.ecom.inventory.entity.InventoryReservation;
import com.ecom.inventory.entity.InventoryTransaction;

/**
 * Implementation of InventoryMapper interface.
 *
 * Handles all data transformation between domain entities and Data Transfer Objects (DTOs).
 * This component ensures clear separation between business logic (service layer) and
 * presentation concerns (controller/API layer).
 *
 * Design Pattern: Component/Converter Pattern
 * Principle Applied:
 * - Single Responsibility Principle (SRP): Focused solely on data transformation
 * - Dependency Inversion Principle (DIP): Depends on abstraction (InventoryMapper interface)
 */
@Component
public class InventoryMapperImpl implements InventoryMapper {

    @Override
    public InventoryResponse mapToInventoryResponse(Inventory inventory) {
        if (inventory == null) {
            return null;
        }

        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .availableQuantity(inventory.getAvailableQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .totalQuantity(inventory.getAvailableQuantity() + inventory.getReservedQuantity())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    @Override
    public ReservationResponse mapToReservationResponse(InventoryReservation reservation) {
        if (reservation == null) {
            return null;
        }

        return ReservationResponse.builder()
                .id(reservation.getId())
                .orderId(reservation.getOrderId())
                .productId(reservation.getProductId())
                .quantity(reservation.getQuantity())
                .status(reservation.getStatus().toString())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }

    @Override
    public TransactionResponse mapToTransactionResponse(InventoryTransaction transaction) {
        if (transaction == null) {
            return null;
        }

        return TransactionResponse.builder()
                .id(transaction.getId())
                .productId(transaction.getProductId())
                .orderId(transaction.getOrderId())
                .quantity(transaction.getQuantity())
                .transactionType(transaction.getTransactionType().toString())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
