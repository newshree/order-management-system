package com.ecom.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.user.entity.UserAddress;

/**
 * Repository for UserAddress entity.
 *
 * Provides database operations for user addresses including CRUD operations
 * and custom queries for managing addresses.
 */
@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {

    /**
     * Find all addresses for a user (ordered by creation date).
     */
    List<UserAddress> findByUserProfileIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Find the default address for a user.
     */
    Optional<UserAddress> findByUserProfileIdAndIsDefaultTrue(UUID userId);

    /**
     * Find a specific address by ID and user ID (for authorization).
     */
    Optional<UserAddress> findByIdAndUserProfileId(UUID addressId, UUID userId);

    /**
     * Count addresses for a user.
     */
    long countByUserProfileId(UUID userId);

    /**
     * Delete all addresses for a user.
     */
    void deleteByUserProfileId(UUID userId);
}
