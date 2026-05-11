package com.ecom.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.user.entity.UserProfile;

/**
 * Repository for UserProfile entity.
 *
 * Provides database operations for user profiles including CRUD operations
 * and custom queries for searching and filtering users.
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    /**
     * Find a user by identity user ID.
     */
    Optional<UserProfile> findByIdentityUserIdAndIsDeletedFalse(UUID identityUserId);

    /**
     * Find a user by email and tenant.
     */
    Optional<UserProfile> findByEmailAndTenantIdAndIsDeletedFalse(String email, UUID tenantId);

    /**
     * Find all users for a tenant (paginated, excluding deleted ones).
     */
    Page<UserProfile> findByTenantIdAndIsDeletedFalse(UUID tenantId, Pageable pageable);

    /**
     * Search users by email.
     */
    @Query("SELECT u FROM UserProfile u WHERE u.tenantId = :tenantId AND LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')) AND u.isDeleted = false")
    Page<UserProfile> searchByEmail(@Param("tenantId") UUID tenantId, @Param("email") String email, Pageable pageable);

    /**
     * Find all active users for a tenant.
     */
    Page<UserProfile> findByTenantIdAndIsActiveTrueAndIsDeletedFalse(UUID tenantId, Pageable pageable);

    /**
     * Check if user exists by identity user ID.
     */
    boolean existsByIdentityUserIdAndIsDeletedFalse(UUID identityUserId);

    /**
     * Count active users for a tenant.
     */
    long countByTenantIdAndIsActiveTrueAndIsDeletedFalse(UUID tenantId);
}
