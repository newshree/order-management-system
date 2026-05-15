package com.ecom.user.service;

import com.ecom.user.dto.request.UserPreferencesUpdateRequest;
import com.ecom.user.dto.response.UserPreferencesResponse;
import java.util.UUID;

/**
 * Service interface for UserPreferences operations.
 *
 * Defines business logic for managing user preferences.
 */
public interface UserPreferencesService {

    /**
     * Get preferences for a user.
     *
     * @param userId the user ID
     * @return the preferences response
     */
    UserPreferencesResponse getPreferences(UUID userId);

    /**
     * Update user preferences.
     *
     * @param userId the user ID
     * @param request the update request
     * @return the updated preferences response
     */
    UserPreferencesResponse updatePreferences(UUID userId, UserPreferencesUpdateRequest request);
}
