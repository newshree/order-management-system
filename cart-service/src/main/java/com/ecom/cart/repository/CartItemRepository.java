package com.ecom.cart.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.cart.entity.CartItem;

/**
 * CartItemRepository - JPA repository for CartItem entity.
 * 
 * Provides database access operations for CartItem entities.
 * Uses Spring Data JPA for CRUD operations and custom queries.
 * 
 * Design Pattern: Repository Pattern
 * - Abstracts database access logic
 * - Provides collection-like interface for cart items
 * - Enables testing with mock implementations
 * 
 * SOLID Principles:
 * - Dependency Inversion: Service depends on abstraction, not direct database access
 * - Interface Segregation: Focused interface with specific query methods
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    /**
     * Finds all items in a specific cart.
     * 
     * @param cartId the UUID of the cart
     * @return List of CartItem entities for the cart
     */
    List<CartItem> findByCartId(UUID cartId);

    /**
     * Finds a specific item by cart ID and product ID.
     * 
     * Used to check if a product already exists in cart.
     * 
     * @param cartId the UUID of the cart
     * @param productId the UUID of the product
     * @return Optional containing the CartItem if found, empty otherwise
     */
    Optional<CartItem> findByCartIdAndProductId(UUID cartId, UUID productId);

    /**
     * Deletes all items from a specific cart.
     * 
     * Used during cart clearing operation.
     * 
     * @param cartId the UUID of the cart
     */
    void deleteByCartId(UUID cartId);

    /**
     * Counts the number of items in a specific cart.
     * 
     * @param cartId the UUID of the cart
     * @return number of items in the cart
     */
    long countByCartId(UUID cartId);
}
