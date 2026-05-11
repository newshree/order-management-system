package com.ecom.user.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * UserMetadata entity for storing flexible key-value metadata for users.
 *
 * This entity allows storing custom attributes for users without modifying
 * the UserProfile schema. Useful for future extensibility and custom data.
 */
@Entity
@Table(
    name = "user_metadata",
    indexes = {
        @Index(name = "idx_metadata_user", columnList = "user_id"),
        @Index(name = "idx_metadata_key", columnList = "meta_key")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"userProfile"})
public class UserMetadata {

    /**
     * Unique identifier for the metadata record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private UUID id;

    /**
     * Reference to the user who owns this metadata.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    /**
     * The key for the metadata.
     */
    @Column(name = "meta_key", nullable = false, length = 255)
    private String key;

    /**
     * The value for the metadata (JSON or plain text).
     */
    @Column(name = "meta_value", nullable = false, columnDefinition = "TEXT")
    private String value;

    /**
     * Timestamp when the metadata was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the metadata was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}