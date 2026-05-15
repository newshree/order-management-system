package com.ecom.cart.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.cart.entity.Cart;

/**
 * CartRepository - JPA repository for Cart entity.
 *
 * Provides database access operations for Cart entities.
 * Uses Spring Data JPA for CRUD operations and custom queries.
 *
 * Design Pattern: Repository Pattern
 * - Abstracts database access logic
 * - Provides collection-like interface
 * - Enables testing with mock implementations
 *
 * SOLID Principles:
 * - Dependency Inversion: Service depends on abstraction, not direct database access
 * - Interface Segregation: Focused interface with specific query methods
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

    /**
     * Finds a cart by user ID with all items eagerly loaded.
     *
     * Since each user has exactly one cart (unique constraint),
     * this method returns Optional containing the cart if it exists.
     * Uses JOIN FETCH to eagerly load cart items in a single query.
     *
     * @param userId the UUID of the user
     * @return Optional containing the Cart with items if found, empty otherwise
     */
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.userId = :userId")
    Optional<Cart> findByUserId(@Param("userId") UUID userId);

    /**
     * Checks if a cart exists for a given user ID.
     *
     * @param userId the UUID of the user
     * @return true if cart exists, false otherwise
     */
    boolean existsByUserId(UUID userId);
}
