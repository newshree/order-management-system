package com.ecom.user.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.user.dto.request.UserProfileCreateRequest;
import com.ecom.user.dto.request.UserProfileUpdateRequest;
import com.ecom.user.dto.response.UserProfileResponse;
import com.ecom.user.entity.UserPreferences;
import com.ecom.user.entity.UserProfile;
import com.ecom.user.enums.ErrorCode;
import com.ecom.user.exception.BadRequestException;
import com.ecom.user.exception.ResourceNotFoundException;
import com.ecom.user.mapper.UserPreferencesMapper;
import com.ecom.user.mapper.UserProfileMapper;
import com.ecom.user.repository.UserAddressRepository;
import com.ecom.user.repository.UserMetadataRepository;
import com.ecom.user.repository.UserPreferencesRepository;
import com.ecom.user.repository.UserProfileRepository;
import com.ecom.user.service.UserProfileService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of UserProfileService.
 *
 * Provides business logic for user profile management operations.
 */
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserPreferencesRepository preferencesRepository;
    private final UserAddressRepository addressRepository;
    private final UserMetadataRepository metadataRepository;
    private final UserProfileMapper userProfileMapper;
    private final UserPreferencesMapper preferencesMapper;

    /**
     * Create a new user profile and initialize default preferences.
     *
     * @param request the create request
     * @param tenantId the tenant ID
     * @return the created user profile response
     * @throws BadRequestException if user already exists
     */
    @Override
    @Transactional
    public UserProfileResponse createUserProfile(UserProfileCreateRequest request, UUID tenantId) {
        UUID identityUserId = UUID.fromString(request.getIdentityUserId());

        // Check if user already exists
        if (userProfileRepository.existsByIdentityUserIdAndIsDeletedFalse(identityUserId)) {
            throw new BadRequestException(ErrorCode.USER_ALREADY_EXISTS,
                    "User with identity ID already exists: " + identityUserId);
        }

        // Check if email already exists for this tenant
        if (userProfileRepository.findByEmailAndTenantIdAndIsDeletedFalse(request.getEmail(), tenantId).isPresent()) {
            throw new BadRequestException(ErrorCode.USER_ALREADY_EXISTS,
                    "User with email already exists: " + request.getEmail());
        }

        // Create user profile
        UserProfile userProfile = userProfileMapper.mapToEntity(request, tenantId);
        UserProfile savedProfile = userProfileRepository.save(userProfile);

        // Create default preferences for the user
        UserPreferences preferences = preferencesMapper.createDefault(savedProfile);
        preferencesRepository.save(preferences);

        return userProfileMapper.mapToResponse(savedProfile);
    }

    /**
     * Get user profile by identity user ID.
     *
     * @param identityUserId the identity user ID
     * @return the user profile response
     * @throws ResourceNotFoundException if user not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserById(UUID identityUserId) {
        UserProfile userProfile = findUserProfile(identityUserId);
        return userProfileMapper.mapToResponse(userProfile);
    }

    /**
     * Update user profile.
     *
     * @param identityUserId the identity user ID
     * @param request the update request
     * @return the updated user profile response
     */
    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(UUID identityUserId, UserProfileUpdateRequest request) {
        UserProfile userProfile = findUserProfile(identityUserId);
        UserProfile updated = userProfileMapper.mapToEntity(userProfile, request);
        UserProfile savedProfile = userProfileRepository.save(updated);
        return userProfileMapper.mapToResponse(savedProfile);
    }

    /**
     * Activate or deactivate a user.
     *
     * @param identityUserId the identity user ID
     * @param isActive the new active status
     * @return the updated user profile response
     */
    @Override
    @Transactional
    public UserProfileResponse setUserStatus(UUID identityUserId, Boolean isActive) {
        UserProfile userProfile = findUserProfile(identityUserId);
        userProfile.setActive(Boolean.TRUE.equals(isActive));
        UserProfile savedProfile = userProfileRepository.save(userProfile);
        return userProfileMapper.mapToResponse(savedProfile);
    }

    /**
     * Soft delete a user (mark as deleted but keep data).
     *
     * @param identityUserId the identity user ID
     */
    @Override
    @Transactional
    public void deleteUser(UUID identityUserId) {
        UserProfile userProfile = findUserProfile(identityUserId);
        userProfile.setDeleted(true);
        userProfile.setActive(false);
        userProfileRepository.save(userProfile);

        // Also delete related data
        addressRepository.deleteByUserProfileId(userProfile.getId());
        preferencesRepository.deleteByUserProfileId(userProfile.getId());
        metadataRepository.deleteByUserProfileId(userProfile.getId());
    }

    /**
     * Get all users for a tenant (paginated).
     *
     * @param tenantId the tenant ID
     * @param pageable pagination information
     * @return page of user profiles
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileResponse> getAllUsers(UUID tenantId, Pageable pageable) {
        return userProfileRepository.findByTenantIdAndIsDeletedFalse(tenantId, pageable)
                .map(userProfileMapper::mapToResponse);
    }

    /**
     * Search users by email.
     *
     * @param tenantId the tenant ID
     * @param email the email to search
     * @param pageable pagination information
     * @return page of matching user profiles
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileResponse> searchUsersByEmail(UUID tenantId, String email, Pageable pageable) {
        return userProfileRepository.searchByEmail(tenantId, email, pageable)
                .map(userProfileMapper::mapToResponse);
    }

    /**
     * Get active users for a tenant.
     *
     * @param tenantId the tenant ID
     * @param pageable pagination information
     * @return page of active user profiles
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileResponse> getActiveUsers(UUID tenantId, Pageable pageable) {
        return userProfileRepository.findByTenantIdAndIsActiveTrueAndIsDeletedFalse(tenantId, pageable)
                .map(userProfileMapper::mapToResponse);
    }

    /**
     * Find user profile by identity user ID (internal helper).
     *
     * @param identityUserId the identity user ID
     * @return the user profile
     * @throws ResourceNotFoundException if user not found
     */
    private UserProfile findUserProfile(UUID identityUserId) {
        return userProfileRepository.findByIdentityUserIdAndIsDeletedFalse(identityUserId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND,
                        "User not found with identity ID: " + identityUserId));
    }
}
