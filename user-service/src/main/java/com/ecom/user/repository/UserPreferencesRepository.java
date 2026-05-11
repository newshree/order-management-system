package com.ecom.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.user.entity.UserPreferences;

/**
 * Repository for UserPreferences entity.
 *
 * Provides database operations for user preferences.
 * Each user has exactly one preferences record.
 */
@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, UUID> {

    /**
     * Find preferences for a user by user ID.
     */
    Optional<UserPreferences> findByUserProfileId(UUID userId);

    /**
     * Check if preferences exist for a user.
     */
    boolean existsByUserProfileId(UUID userId);

    /**
     * Delete preferences for a user.
     */
    void deleteByUserProfileId(UUID userId);
}
