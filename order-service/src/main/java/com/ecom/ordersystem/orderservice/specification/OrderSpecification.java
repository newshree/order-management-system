package com.ecom.ordersystem.orderservice.specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import com.ecom.ordersystem.orderservice.enums.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import com.ecom.ordersystem.orderservice.entity.Order;

/**
 * OrderSpecification - Dynamic query builder for Order filtering.
 * 
 * Implements Specification pattern for constructing complex database queries.
 * Enables flexible filtering on multiple attributes without method explosion.
 * 
 * Benefits:
 * - Supports any combination of filters
 * - Type-safe query construction
 * - Reusable filter specifications
 * - Easy to test and maintain
 * 
 * Design Pattern:
 * - Specification Pattern: Encapsulates query logic
 * - Builder Pattern: Chainable filter building
 * 
 * SOLID Principles:
 * - Single Responsibility: Each specification focuses on one filter criterion
 * - Open/Closed: Easy to add new filter specifications without modifying existing code
 * - Dependency Inversion: Uses Spring Data JPA abstraction (Specification interface)
 */
public class OrderSpecification {

    /**
     * Creates specification for filtering by order status.
     * 
     * @param status the OrderStatus to filter by
     * @return Specification for status filtering
     */
    public static Specification<Order> byStatus(OrderStatus status) {
        return (root, query, criteriaBuilder) -> 
            status == null ? criteriaBuilder.conjunction() : 
            criteriaBuilder.equal(root.get("status"), status);
    }

    /**
     * Creates specification for filtering by user ID.
     * 
     * Returns all orders placed by a specific customer.
     * 
     * @param userId the UUID of the user/customer
     * @return Specification for user ID filtering
     */
    public static Specification<Order> byUserId(UUID userId) {
        return (root, query, criteriaBuilder) -> 
            userId == null ? criteriaBuilder.conjunction() : 
            criteriaBuilder.equal(root.get("userId"), userId);
    }

    /**
     * Creates specification for filtering by order number.
     * 
     * Supports partial matching (LIKE query).
     * For example, searching "ORD-2026" would match all orders from 2026.
     * 
     * @param orderNumber the order number to search for (supports partial)
     * @return Specification for order number filtering
     */
    public static Specification<Order> byOrderNumber(String orderNumber) {
        return (root, query, criteriaBuilder) -> 
            orderNumber == null || orderNumber.isEmpty() ? criteriaBuilder.conjunction() : 
            criteriaBuilder.like(criteriaBuilder.lower(root.get("orderNumber")), 
                "%" + orderNumber.toLowerCase() + "%");
    }

    /**
     * Creates specification for filtering by date range.
     * 
     * Filters orders created within the specified date range (inclusive).
     * Start date is beginning of day (00:00:00), end date is end of day (23:59:59).
     * 
     * @param startDate the start date (inclusive), or null for no lower bound
     * @param endDate the end date (inclusive), or null for no upper bound
     * @return Specification for date range filtering
     */
    public static Specification<Order> byDateRange(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            // If no dates specified, return true (no filter)
            if (startDate == null && endDate == null) {
                return criteriaBuilder.conjunction();
            }

            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;

            // Convert start date to beginning of day
            if (startDate != null) {
                startDateTime = startDate.atStartOfDay();
            }

            // Convert end date to end of day
            if (endDate != null) {
                endDateTime = endDate.atTime(LocalTime.MAX);
            }

            // Build predicate based on available dates
            if (startDateTime != null && endDateTime != null) {
                // Both dates provided: range between them
                return criteriaBuilder.between(root.get("createdAt"), startDateTime, endDateTime);
            } else if (startDateTime != null) {
                // Only start date: from start date onwards
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDateTime);
            } else {
                // Only end date: up to end date
                return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDateTime);
            }
        };
    }

    /**
     * Combines multiple specifications using AND logic.
     * 
     * All filters must match for the order to be included in results.
     * This is useful for building complex queries with multiple criteria.
     * 
     * @param spec1 first specification
     * @param spec2 second specification
     * @return Combined specification using AND logic
     */
    public static Specification<Order> and(Specification<Order> spec1, Specification<Order> spec2) {
        if (spec1 == null) return spec2;
        if (spec2 == null) return spec1;
        return spec1.and(spec2);
    }
}

