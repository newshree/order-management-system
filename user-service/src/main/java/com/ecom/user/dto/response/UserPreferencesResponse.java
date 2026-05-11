package com.ecom.user.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for UserPreferences.
 *
 * This DTO is returned when fetching user preferences.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferencesResponse {

    /**
     * Unique identifier for the preferences.
     */
    private UUID id;

    /**
     * Preferred language.
     */
    private String language;

    /**
     * Preferred currency.
     */
    private String currency;

    /**
     * Email notifications enabled status.
     */
    private Boolean emailNotificationsEnabled;

    /**
     * SMS notifications enabled status.
     */
    private Boolean smsNotificationsEnabled;

    /**
     * Timestamp when the preferences were created.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * Timestamp when the preferences were last updated.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
