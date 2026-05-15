package com.ecom.user.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ecom.user.dto.request.UserProfileCreateRequest;
import com.ecom.user.dto.request.UserProfileUpdateRequest;
import com.ecom.user.dto.response.UserProfileResponse;
import com.ecom.user.entity.UserProfile;

/**
 * Mapper for UserProfile entity to/from DTOs.
 *
 * Converts between UserProfile entity and its request/response DTOs.
 */
@Component
public class UserProfileMapper {

    /**
     * Convert UserProfileCreateRequest to UserProfile entity.
     *
     * @param request the create request DTO
     * @param tenantId the tenant ID
     * @return the UserProfile entity
     */
    public UserProfile mapToEntity(UserProfileCreateRequest request, UUID tenantId) {
        return UserProfile.builder()
                .identityUserId(UUID.fromString(request.getIdentityUserId()))
                .tenantId(tenantId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .isActive(true)
                .isDeleted(false)
                .build();
    }

    /**
     * Update UserProfile entity from UserProfileUpdateRequest.
     *
     * @param entity the entity to update
     * @param request the update request DTO
     * @return the updated UserProfile entity
     */
    public UserProfile mapToEntity(UserProfile entity, UserProfileUpdateRequest request) {
        if (request.getFirstName() != null) {
            entity.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            entity.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            entity.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDateOfBirth() != null) {
            entity.setDateOfBirth(request.getDateOfBirth());
        }
        return entity;
    }

    /**
     * Convert UserProfile entity to UserProfileResponse DTO.
     *
     * @param entity the entity to convert
     * @return the response DTO
     */
    public UserProfileResponse mapToResponse(UserProfile entity) {
        return UserProfileResponse.builder()
                .id(entity.getId())
                .identityUserId(entity.getIdentityUserId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .dateOfBirth(entity.getDateOfBirth())
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
