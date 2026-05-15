package com.ecom.user.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.user.dto.request.UserPreferencesUpdateRequest;
import com.ecom.user.dto.response.UserPreferencesResponse;
import com.ecom.user.entity.UserPreferences;
import com.ecom.user.enums.ErrorCode;
import com.ecom.user.exception.ResourceNotFoundException;
import com.ecom.user.mapper.UserPreferencesMapper;
import com.ecom.user.repository.UserPreferencesRepository;
import com.ecom.user.repository.UserProfileRepository;
import com.ecom.user.service.UserPreferencesService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of UserPreferencesService.
 *
 * Provides business logic for user preferences management operations.
 */
@Service
@RequiredArgsConstructor
public class UserPreferencesServiceImpl implements UserPreferencesService {

    private final UserPreferencesRepository preferencesRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserPreferencesMapper preferencesMapper;

    /**
     * Get preferences for a user.
     *
     * @param userId the user ID
     * @return the preferences response
     * @throws ResourceNotFoundException if preferences not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserPreferencesResponse getPreferences(UUID userId) {
        UserPreferences preferences = findUserPreferences(userId);
        return preferencesMapper.mapToResponse(preferences);
    }

    /**
     * Update user preferences.
     *
     * @param userId the user ID
     * @param request the update request
     * @return the updated preferences response
     */
    @Override
    @Transactional
    public UserPreferencesResponse updatePreferences(UUID userId, UserPreferencesUpdateRequest request) {
        UserPreferences preferences = findUserPreferences(userId);
        UserPreferences updated = preferencesMapper.mapToEntity(preferences, request);
        UserPreferences savedPreferences = preferencesRepository.save(updated);
        return preferencesMapper.mapToResponse(savedPreferences);
    }

    /**
     * Find user preferences by user ID (internal helper).
     *
     * @param userId the user ID
     * @return the preferences
     * @throws ResourceNotFoundException if preferences not found
     */
    private UserPreferences findUserPreferences(UUID userId) {
        // First verify user exists
        userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND,
                        "User not found: " + userId));

        // Then get preferences
        return preferencesRepository.findByUserProfileId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PREFERENCES_NOT_FOUND,
                        "Preferences not found for user: " + userId));
    }
}
