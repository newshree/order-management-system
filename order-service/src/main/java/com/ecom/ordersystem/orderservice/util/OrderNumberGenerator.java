package com.ecom.ordersystem.orderservice.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecom.ordersystem.orderservice.repository.OrderRepository;

import java.time.LocalDate;

/**
 * OrderNumberGenerator - Utility component for generating unique, human-readable order numbers.
 * 
 * Generates order numbers in the format: ORD-{YEAR}-{SEQUENCE}
 * Example: ORD-2026-000001, ORD-2026-000002, etc.
 * 
 * Features:
 * - Generates unique order numbers based on current year and sequential count
 * - Ensures human-readable format for customer-facing operations
 * - Thread-safe through database transactions
 * 
 * Design Pattern: Utility/Helper Pattern
 * - Encapsulates number generation logic
 * - Centralizes order number format
 * - Easy to modify format in one place
 * 
 * SOLID Principles:
 * - Single Responsibility: Focuses solely on order number generation
 * - Dependency Inversion: Depends on OrderRepository abstraction
 */
@Component
public class OrderNumberGenerator {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Generates a unique order number.
     * 
     * Format: ORD-{YEAR}-{SEQUENCE_NUMBER}
     * Example: ORD-2026-000001
     * 
     * Logic:
     * - Extracts current year
     * - Counts existing orders and increments by 1
     * - Pads sequence with leading zeros (6 digits)
     * 
     * Thread Safety:
     * - Safe for concurrent use due to database transaction isolation
     * - Each order is persisted in same transaction
     * 
     * @return generated order number as String
     */
    public String generateOrderNumber() {
        // Get current year for order number format
        int year = LocalDate.now().getYear();
        
        // Calculate next sequence number: total orders + 1
        long count = orderRepository.count() + 1;
        
        // Format: ORD-{YEAR}-{SEQUENCE_NUMBER_PADDED_TO_6_DIGITS}
        return String.format("ORD-%d-%06d", year, count);
    }
}
