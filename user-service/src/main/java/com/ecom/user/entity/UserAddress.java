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
 * UserAddress entity representing a shipping/billing address for a user.
 *
 * Each user can have multiple addresses, but only one default address.
 * This entity supports CRUD operations with validation for one-default-address constraint.
 */
@Entity
@Table(
    name = "user_addresses",
    indexes = {
        @Index(name = "idx_address_user", columnList = "user_id"),
        @Index(name = "idx_address_default", columnList = "is_default"),
        @Index(name = "idx_address_user_default", columnList = "user_id, is_default")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"userProfile"})
public class UserAddress {

    /**
     * Unique identifier for the address.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private UUID id;

    /**
     * Reference to the user who owns this address.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    /**
     * Full name for this address (recipient name).
     */
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    /**
     * Phone number for this address.
     */
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    /**
     * First line of the address.
     */
    @Column(name = "address_line1", nullable = false, length = 500)
    private String addressLine1;

    /**
     * Second line of the address (optional, apartment/suite number, etc.).
     */
    @Column(name = "address_line2", length = 500)
    private String addressLine2;

    /**
     * Landmark for the address (optional).
     */
    @Column(name = "landmark", length = 500)
    private String landmark;

    /**
     * City of the address.
     */
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    /**
     * State/Province of the address.
     */
    @Column(name = "state", nullable = false, length = 100)
    private String state;

    /**
     * Country of the address.
     */
    @Column(name = "country", nullable = false, length = 100)
    private String country;

    /**
     * Postal code/ZIP code of the address.
     */
    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    /**
     * Indicates if this is the default address for the user.
     * Only one address per user can be marked as default.
     */
    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private boolean isDefault = false;

    /**
     * Indicates if the address is soft deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    /**
     * Timestamp when the address was deleted.
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Timestamp when the address was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the address was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}