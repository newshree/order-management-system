package com.ecom.user.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ecom.user.dto.request.UserProfileCreateRequest;
import com.ecom.user.dto.request.UserProfileUpdateRequest;
import com.ecom.user.dto.response.UserProfileResponse;

/**
 * Service interface for UserProfile operations.
 *
 * Defines business logic for managing user profiles.
 */
public interface UserProfileService {

    /**
     * Create a new user profile.
     *
     * @param request the create request
     * @param tenantId the tenant ID
     * @return the created user profile response
     */
    UserProfileResponse createUserProfile(UserProfileCreateRequest request, UUID tenantId);

    /**
     * Get user profile by identity user ID.
     *
     * @param identityUserId the identity user ID
     * @return the user profile response
     */
    UserProfileResponse getUserById(UUID identityUserId);

    /**
     * Update user profile.
     *
     * @param identityUserId the identity user ID
     * @param request the update request
     * @return the updated user profile response
     */
    UserProfileResponse updateUserProfile(UUID identityUserId, UserProfileUpdateRequest request);

    /**
     * Activate or deactivate a user.
     *
     * @param identityUserId the identity user ID
     * @param isActive the new active status
     * @return the updated user profile response
     */
    UserProfileResponse setUserStatus(UUID identityUserId, Boolean isActive);

    /**
     * Soft delete a user.
     *
     * @param identityUserId the identity user ID
     */
    void deleteUser(UUID identityUserId);

    /**
     * Get all users for a tenant (paginated).
     *
     * @param tenantId the tenant ID
     * @param pageable pagination information
     * @return page of user profiles
     */
    Page<UserProfileResponse> getAllUsers(UUID tenantId, Pageable pageable);

    /**
     * Search users by email.
     *
     * @param tenantId the tenant ID
     * @param email the email to search
     * @param pageable pagination information
     * @return page of matching user profiles
     */
    Page<UserProfileResponse> searchUsersByEmail(UUID tenantId, String email, Pageable pageable);

    /**
     * Get active users for a tenant.
     *
     * @param tenantId the tenant ID
     * @param pageable pagination information
     * @return page of active user profiles
     */
    Page<UserProfileResponse> getActiveUsers(UUID tenantId, Pageable pageable);
}
