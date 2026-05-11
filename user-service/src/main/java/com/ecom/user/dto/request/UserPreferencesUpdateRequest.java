package com.ecom.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating user preferences.
 *
 * This DTO is used to update language, currency, and notification preferences.
 * All fields are optional to support partial updates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferencesUpdateRequest {

    /**
     * Preferred language (e.g., 'en', 'fr', 'es').
     */
    private String language;

    /**
     * Preferred currency (e.g., 'USD', 'EUR', 'INR').
     */
    private String currency;

    /**
     * Enable or disable email notifications.
     */
    private Boolean emailNotificationsEnabled;

    /**
     * Enable or disable SMS notifications.
     */
    private Boolean smsNotificationsEnabled;
}
