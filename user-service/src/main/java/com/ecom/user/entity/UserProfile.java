package com.ecom.user.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * UserProfile entity representing a user in the system.
 *
 * Each user is uniquely identified by their identity service user ID,
 * email, and tenant. Contains personal information and account status.
 */
@Entity
@Table(
    name = "user_profiles",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "email"}),
        @UniqueConstraint(columnNames = {"identity_user_id"})
    },
    indexes = {
        @Index(name = "idx_user_tenant", columnList = "tenant_id"),
        @Index(name = "idx_user_identity_id", columnList = "identity_user_id"),
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_active", columnList = "is_active")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserProfile {

    /**
     * Unique identifier for the user profile.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private UUID id;

    /**
     * User ID from Identity Service.
     * This links the user profile to the Identity/Auth service.
     */
    @Column(name = "identity_user_id", nullable = false, unique = true)
    private UUID identityUserId;

    /**
     * Tenant ID for multi-tenancy support.
     */
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    /**
     * User's first name.
     */
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    /**
     * User's last name.
     */
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /**
     * User's email address (unique per tenant).
     */
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    /**
     * User's phone number.
     */
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    /**
     * User's date of birth.
     */
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    /**
     * Indicates if the user account is active.
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    /**
     * Indicates if the user is soft deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    /**
     * Timestamp when the user was deleted.
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * User addresses.
     */
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserAddress> addresses = new ArrayList<>();

    /**
     * User preferences.
     */
    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL)
    private UserPreferences preferences;

    /**
     * User metadata entries.
     */
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserMetadata> metadata = new ArrayList<>();

    /**
     * Timestamp when the user profile was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user profile was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}