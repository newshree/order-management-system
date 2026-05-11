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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * UserPreferences entity representing user preferences and notification settings.
 *
 * Each user has exactly one preferences record containing language, currency,
 * and notification preferences.
 */
@Entity
@Table(
    name = "user_preferences",
    indexes = {
        @Index(name = "idx_preferences_user", columnList = "user_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"userProfile"})
public class UserPreferences {

    /**
     * Unique identifier for the preferences.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private UUID id;

    /**
     * Reference to the user who owns these preferences.
     * One-to-one relationship with UserProfile.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserProfile userProfile;

    /**
     * Preferred language (e.g., 'en', 'fr', 'es').
     */
    @Column(name = "language", nullable = false, length = 10)
    @Builder.Default
    private String language = "en";

    /**
     * Preferred currency (e.g., 'USD', 'EUR', 'INR').
     */
    @Column(name = "currency", nullable = false, length = 10)
    @Builder.Default
    private String currency = "INR";

    /**
     * Indicates if email notifications are enabled.
     */
    @Column(name = "email_notifications_enabled", nullable = false)
    @Builder.Default
    private boolean emailNotificationsEnabled = true;

    /**
     * Indicates if SMS notifications are enabled.
     */
    @Column(name = "sms_notifications_enabled", nullable = false)
    @Builder.Default
    private boolean smsNotificationsEnabled = false;

    /**
     * Timestamp when the preferences were created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the preferences were last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}