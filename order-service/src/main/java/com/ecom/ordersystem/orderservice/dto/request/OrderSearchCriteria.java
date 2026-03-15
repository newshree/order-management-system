package com.ecom.ordersystem.orderservice.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import com.ecom.ordersystem.orderservice.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrderSearchCriteria - Data Transfer Object for order search/filter requests.
 * 
 * Encapsulates all possible filter parameters for admin order search.
 * All fields are optional - when null, the filter is not applied.
 * 
 * Usage:
 * - Used in admin order search endpoints
 * - Supports flexible filtering with any combination of criteria
 * - Easily extensible for additional filter types
 * 
 * Design Pattern:
 * - Data Transfer Object (DTO) Pattern
 * - Criteria Pattern: Encapsulates search criteria
 * - Builder Pattern (via Lombok): Easy object construction
 * 
 * SOLID Principles:
 * - Single Responsibility: Represents search criteria only
 * - Interface Segregation: Contains only filter parameters
 * - Open/Closed: Easy to add new criteria fields
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSearchCriteria {

    /**
     * Filter by order status.
     * 
     * When specified, only orders with this status are returned.
     * 
     * - Type: OrderStatus enum
     * - Required: No
     * - Example: SHIPPED, DELIVERED, PENDING
     * 
     * @see com.ecom.ordersystem.orderservice.enums.OrderStatus
     */
    private OrderStatus status;

    /**
     * Filter by customer/user ID.
     * 
     * When specified, only orders from this customer are returned.
     * Useful for customer-specific order reports.
     * 
     * - Type: UUID
     * - Required: No
     * - Example: "550e8400-e29b-41d4-a716-446655440000"
     */
    private UUID userId;

    /**
     * Filter by order number (supports partial matching).
     * 
     * When specified, returns orders matching this order number.
     * Supports partial matching (LIKE query).
     * For example, "ORD-2026" matches all orders from 2026.
     * 
     * - Type: String
     * - Required: No
     * - Matching: Case-insensitive partial match
     * - Example: "ORD-2026-001"
     */
    private String orderNumber;

    /**
     * Filter by order creation date (start of range).
     * 
     * When specified with or without endDate, filters orders created on or after this date.
     * If only startDate is specified: orders from startDate onwards
     * If both are specified: orders between startDate and endDate (inclusive)
     * 
     * - Type: LocalDate
     * - Required: No
     * - Format: YYYY-MM-DD
     * - Time Component: Treated as 00:00:00 (start of day)
     * - Example: "2026-03-01"
     */
    private LocalDate startDate;

    /**
     * Filter by order creation date (end of range).
     * 
     * When specified with or without startDate, filters orders created on or before this date.
     * If only endDate is specified: orders up to endDate
     * If both are specified: orders between startDate and endDate (inclusive)
     * 
     * - Type: LocalDate
     * - Required: No
     * - Format: YYYY-MM-DD
     * - Time Component: Treated as 23:59:59 (end of day)
     * - Example: "2026-03-10"
     */
    private LocalDate endDate;
}

