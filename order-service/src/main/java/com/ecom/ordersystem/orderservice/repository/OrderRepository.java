package com.ecom.ordersystem.orderservice.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ecom.ordersystem.orderservice.entity.Order;

/**
 * OrderRepository - Data access layer for Order entities.
 * 
 * Provides abstraction for database operations on Order entities.
 * Extends Spring Data JPA's JpaRepository for basic CRUD operations
 * and JpaSpecificationExecutor for advanced dynamic filtering.
 * Adds custom query methods for business-specific queries.
 * 
 * Design Pattern: Repository Pattern
 * - Abstracts data access logic
 * - Provides a collection-like interface
 * - Enables easier testing with mock implementations
 * - JpaSpecificationExecutor enables dynamic query building (Specification Pattern)
 * 
 * SOLID Principles:
 * - Dependency Inversion: Service layer depends on this abstraction, not direct database access
 * - Interface Segregation: Focused interface with specific query methods
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    /**
     * Finds an order by its unique order number.
     * 
     * Order numbers are human-readable identifiers (e.g., ORD-2026-000001)
     * used for customer-facing operations.
     * 
     * @param orderNumber the order number to search for
     * @return Optional containing the Order if found, empty otherwise
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Finds all orders for a specific user with pagination support.
     * 
     * Useful for customer portals and admin dashboards.
     * Supports efficient pagination for large datasets.
     * 
     * @param userId the UUID of the customer
     * @param pageable pagination information (page number, size, sorting)
     * @return Page of Order entities for the user
     */
    Page<Order> findByUserId(UUID userId, Pageable pageable);
}


