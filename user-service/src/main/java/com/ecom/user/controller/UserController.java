package com.ecom.user.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.user.dto.request.UserProfileUpdateRequest;
import com.ecom.user.dto.response.ApiResponse;
import com.ecom.user.dto.response.UserProfileResponse;
import com.ecom.user.service.UserProfileService;
import com.ecom.user.util.UserContext;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for User operations.
 *
 * Provides endpoints for user profile management (self-service).
 * Users can view and update their own profiles via these endpoints.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userProfileService;
    private final UserContext userContext;

    /**
     * Get current user's profile (authenticated user).
     *
     * @return API response with user profile
     */
    @GetMapping("/getCurrentUser/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUserProfile() {
        UUID userId = userContext.getUserIdFromHeader();
        UserProfileResponse profile = userProfileService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.<UserProfileResponse>builder()
                .success(true)
                .data(profile)
                .message("User profile retrieved successfully")
                .build());
    }

    /**
     * Update current user's profile.
     *
     * @param request the update request
     * @return API response with updated user profile
     */
    @PutMapping("/updateUser/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUserProfile(
            @Valid @RequestBody UserProfileUpdateRequest request) {
        UUID userId = userContext.getUserIdFromHeader();
        UserProfileResponse profile = userProfileService.updateUserProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.<UserProfileResponse>builder()
                .success(true)
                .data(profile)
                .message("User profile updated successfully")
                .build());
    }
}
