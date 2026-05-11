package com.ecom.user.mapper;

import org.springframework.stereotype.Component;

import com.ecom.user.dto.request.UserAddressCreateRequest;
import com.ecom.user.dto.request.UserAddressUpdateRequest;
import com.ecom.user.dto.response.UserAddressResponse;
import com.ecom.user.entity.UserAddress;
import com.ecom.user.entity.UserProfile;

/**
 * Mapper for UserAddress entity to/from DTOs.
 *
 * Converts between UserAddress entity and its request/response DTOs.
 */
@Component
public class UserAddressMapper {

    /**
     * Convert UserAddressCreateRequest to UserAddress entity.
     *
     * @param request the create request DTO
     * @param userProfile the user profile associated with this address
     * @return the UserAddress entity
     */
    public UserAddress mapToEntity(UserAddressCreateRequest request, UserProfile userProfile) {
        return UserAddress.builder()
                .userProfile(userProfile)
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .landmark(request.getLandmark())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .isDefault(Boolean.TRUE.equals(request.getIsDefault()))
                .build();
    }

    /**
     * Update UserAddress entity from UserAddressUpdateRequest.
     *
     * @param entity the entity to update
     * @param request the update request DTO
     * @return the updated UserAddress entity
     */
    public UserAddress mapToEntity(UserAddress entity, UserAddressUpdateRequest request) {
        if (request.getFullName() != null) {
            entity.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            entity.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddressLine1() != null) {
            entity.setAddressLine1(request.getAddressLine1());
        }
        if (request.getAddressLine2() != null) {
            entity.setAddressLine2(request.getAddressLine2());
        }
        if (request.getLandmark() != null) {
            entity.setLandmark(request.getLandmark());
        }
        if (request.getCity() != null) {
            entity.setCity(request.getCity());
        }
        if (request.getState() != null) {
            entity.setState(request.getState());
        }
        if (request.getCountry() != null) {
            entity.setCountry(request.getCountry());
        }
        if (request.getPostalCode() != null) {
            entity.setPostalCode(request.getPostalCode());
        }
        return entity;
    }

    /**
     * Convert UserAddress entity to UserAddressResponse DTO.
     *
     * @param entity the entity to convert
     * @return the response DTO
     */
    public UserAddressResponse mapToResponse(UserAddress entity) {
        return UserAddressResponse.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .phoneNumber(entity.getPhoneNumber())
                .addressLine1(entity.getAddressLine1())
                .addressLine2(entity.getAddressLine2())
                .landmark(entity.getLandmark())
                .city(entity.getCity())
                .state(entity.getState())
                .country(entity.getCountry())
                .postalCode(entity.getPostalCode())
                .isDefault(entity.isDefault())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
