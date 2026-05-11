package com.ecom.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.user.entity.UserMetadata;

/**
 * Repository for UserMetadata entity.
 *
 * Provides database operations for user metadata (key-value pairs).
 */
@Repository
public interface UserMetadataRepository extends JpaRepository<UserMetadata, UUID> {

    /**
     * Find all metadata for a user.
     */
    List<UserMetadata> findByUserProfileId(UUID userId);

    /**
     * Find metadata by user ID and key.
     */
    Optional<UserMetadata> findByUserProfileIdAndKey(UUID userId, String key);

    /**
     * Delete all metadata for a user.
     */
    void deleteByUserProfileId(UUID userId);

    /**
     * Delete metadata by user ID and key.
     */
    void deleteByUserProfileIdAndKey(UUID userId, String key);
}
