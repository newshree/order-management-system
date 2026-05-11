package com.ecom.user.mapper;

import org.springframework.stereotype.Component;

import com.ecom.user.dto.request.UserPreferencesUpdateRequest;
import com.ecom.user.dto.response.UserPreferencesResponse;
import com.ecom.user.entity.UserPreferences;
import com.ecom.user.entity.UserProfile;

/**
 * Mapper for UserPreferences entity to/from DTOs.
 *
 * Converts between UserPreferences entity and its request/response DTOs.
 */
@Component
public class UserPreferencesMapper {

    /**
     * Create default UserPreferences entity for a new user.
     *
     * @param userProfile the user profile associated with these preferences
     * @return the UserPreferences entity with default values
     */
    public UserPreferences createDefault(UserProfile userProfile) {
        return UserPreferences.builder()
                .userProfile(userProfile)
                .language("en")
                .currency("USD")
                .emailNotificationsEnabled(true)
                .smsNotificationsEnabled(false)
                .build();
    }

    /**
     * Update UserPreferences entity from UserPreferencesUpdateRequest.
     *
     * @param entity the entity to update
     * @param request the update request DTO
     * @return the updated UserPreferences entity
     */
    public UserPreferences mapToEntity(UserPreferences entity, UserPreferencesUpdateRequest request) {
        if (request.getLanguage() != null) {
            entity.setLanguage(request.getLanguage());
        }
        if (request.getCurrency() != null) {
            entity.setCurrency(request.getCurrency());
        }
        if (request.getEmailNotificationsEnabled() != null) {
            entity.setEmailNotificationsEnabled(request.getEmailNotificationsEnabled());
        }
        if (request.getSmsNotificationsEnabled() != null) {
            entity.setSmsNotificationsEnabled(request.getSmsNotificationsEnabled());
        }
        return entity;
    }

    /**
     * Convert UserPreferences entity to UserPreferencesResponse DTO.
     *
     * @param entity the entity to convert
     * @return the response DTO
     */
    public UserPreferencesResponse mapToResponse(UserPreferences entity) {
        return UserPreferencesResponse.builder()
                .id(entity.getId())
                .language(entity.getLanguage())
                .currency(entity.getCurrency())
                .emailNotificationsEnabled(entity.isEmailNotificationsEnabled())
                .smsNotificationsEnabled(entity.isSmsNotificationsEnabled())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
