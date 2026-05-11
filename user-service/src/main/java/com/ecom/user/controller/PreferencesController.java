package com.ecom.user.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.user.dto.request.UserPreferencesUpdateRequest;
import com.ecom.user.dto.response.ApiResponse;
import com.ecom.user.dto.response.UserPreferencesResponse;
import com.ecom.user.service.UserPreferencesService;
import com.ecom.user.util.UserContext;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for User Preferences operations.
 *
 * Provides endpoints for managing user preferences (self-service).
 */
@RestController
@RequestMapping("/api/users/me/preferences")
@RequiredArgsConstructor
public class PreferencesController {

    private final UserPreferencesService preferencesService;
    private final UserContext userContext;

    /**
     * Get current user's preferences.
     *
     * @return API response with user preferences
     */
    @GetMapping("/getPreferences")
    public ResponseEntity<ApiResponse<UserPreferencesResponse>> getPreferences() {
        UUID userId = userContext.getUserIdFromHeader();
        UserPreferencesResponse preferences = preferencesService.getPreferences(userId);
        return ResponseEntity.ok(ApiResponse.<UserPreferencesResponse>builder()
                .success(true)
                .data(preferences)
                .message("User preferences retrieved successfully")
                .build());
    }

    /**
     * Update current user's preferences.
     *
     * @param request the update request
     * @return API response with updated preferences
     */
    @PutMapping("/updatePreferences")
    public ResponseEntity<ApiResponse<UserPreferencesResponse>> updatePreferences(
            @Valid @RequestBody UserPreferencesUpdateRequest request) {
        UUID userId = userContext.getUserIdFromHeader();
        UserPreferencesResponse preferences = preferencesService.updatePreferences(userId, request);
        return ResponseEntity.ok(ApiResponse.<UserPreferencesResponse>builder()
                .success(true)
                .data(preferences)
                .message("User preferences updated successfully")
                .build());
    }
}
