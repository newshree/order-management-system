package com.ecom.cart.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ecom.cart.redis.RedisCart;

/**
 * RedisCartRepository - Redis repository for RedisCart entity.
 * 
 * Provides fast in-memory access operations for cart data.
 * Uses Spring Data Redis for CRUD operations with automatic TTL management.
 * 
 * Features:
 * - Automatic serialization/deserialization
 * - TTL management (7 days)
 * - Fast read/write operations
 * - Expiration handling
 * 
 * Design Pattern: Repository Pattern
 * - Abstracts Redis access logic
 * - Provides collection-like interface
 * - Enables fast cart access
 * 
 * SOLID Principles:
 * - Dependency Inversion: Service depends on abstraction
 * - Interface Segregation: Focused on Redis operations
 * 
 * Usage:
 * - Primary cart storage for active operations
 * - Automatic expiration after 7 days
 * - Faster than database for frequent operations
 */
@Repository
public interface RedisCartRepository extends CrudRepository<RedisCart, String> {

    /**
     * Finds a cart by user ID in Redis.
     * 
     * The userId is used directly as the Redis key.
     * 
     * @param userId the user ID as string
     * @return Optional containing the RedisCart if found, empty Optional if expired or not exists
     */
    @Override
    Optional<RedisCart> findById(String userId);

    /**
     * Saves or updates a cart in Redis.
     * 
     * Automatically manages TTL (7 days).
     * 
     * @param cart the RedisCart to save
     * @return the saved RedisCart
     */
    @Override
    RedisCart save(RedisCart cart);

    /**
     * Deletes a cart from Redis.
     * 
     * @param userId the user ID as string
     */
    void deleteById(String userId);
}
