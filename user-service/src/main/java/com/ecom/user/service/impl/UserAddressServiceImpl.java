package com.ecom.user.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.user.dto.request.UserAddressCreateRequest;
import com.ecom.user.dto.request.UserAddressUpdateRequest;
import com.ecom.user.dto.response.UserAddressResponse;
import com.ecom.user.entity.UserAddress;
import com.ecom.user.entity.UserProfile;
import com.ecom.user.enums.ErrorCode;
import com.ecom.user.exception.ResourceNotFoundException;
import com.ecom.user.mapper.UserAddressMapper;
import com.ecom.user.repository.UserAddressRepository;
import com.ecom.user.repository.UserProfileRepository;
import com.ecom.user.service.UserAddressService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of UserAddressService.
 *
 * Provides business logic for user address management operations.
 */
@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository addressRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserAddressMapper addressMapper;

    /**
     * Add a new address for a user.
     *
     * @param userId the user ID
     * @param request the create request
     * @return the created address response
     * @throws ResourceNotFoundException if user not found
     */
    @Override
    @Transactional
    public UserAddressResponse addAddress(UUID userId, UserAddressCreateRequest request) {
        UserProfile userProfile = findUserProfile(userId);

        // If this address is marked as default, remove default flag from all other addresses
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            removeDefaultFlagFromOtherAddresses(userId);
        }

        UserAddress address = addressMapper.mapToEntity(request, userProfile);
        UserAddress savedAddress = addressRepository.save(address);
        return addressMapper.mapToResponse(savedAddress);
    }

    /**
     * Get all addresses for a user.
     *
     * @param userId the user ID
     * @return list of address responses
     * @throws ResourceNotFoundException if user not found
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserAddressResponse> getUserAddresses(UUID userId) {
        findUserProfile(userId); // Verify user exists
        return addressRepository.findByUserProfileIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(addressMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get default address for a user.
     *
     * @param userId the user ID
     * @return the default address response
     * @throws ResourceNotFoundException if user or default address not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserAddressResponse getDefaultAddress(UUID userId) {
        findUserProfile(userId); // Verify user exists
        UserAddress address = addressRepository.findByUserProfileIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.NO_DEFAULT_ADDRESS,
                        "No default address found for user: " + userId));
        return addressMapper.mapToResponse(address);
    }

    /**
     * Get a specific address by ID.
     *
     * @param userId the user ID
     * @param addressId the address ID
     * @return the address response
     * @throws ResourceNotFoundException if address not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserAddressResponse getAddressById(UUID userId, UUID addressId) {
        findUserProfile(userId); // Verify user exists
        UserAddress address = addressRepository.findByIdAndUserProfileId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ADDRESS_NOT_FOUND,
                        "Address not found: " + addressId));
        return addressMapper.mapToResponse(address);
    }

    /**
     * Update an address.
     *
     * @param userId the user ID
     * @param addressId the address ID
     * @param request the update request
     * @return the updated address response
     */
    @Override
    @Transactional
    public UserAddressResponse updateAddress(UUID userId, UUID addressId, UserAddressUpdateRequest request) {
        findUserProfile(userId); // Verify user exists
        UserAddress address = addressRepository.findByIdAndUserProfileId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ADDRESS_NOT_FOUND,
                        "Address not found: " + addressId));

        UserAddress updated = addressMapper.mapToEntity(address, request);
        UserAddress savedAddress = addressRepository.save(updated);
        return addressMapper.mapToResponse(savedAddress);
    }

    /**
     * Delete an address.
     *
     * @param userId the user ID
     * @param addressId the address ID
     */
    @Override
    @Transactional
    public void deleteAddress(UUID userId, UUID addressId) {
        findUserProfile(userId); // Verify user exists
        UserAddress address = addressRepository.findByIdAndUserProfileId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ADDRESS_NOT_FOUND,
                        "Address not found: " + addressId));

        // If deleting default address, make another one default if available
        if (address.isDefault()) {
            List<UserAddress> otherAddresses = addressRepository.findByUserProfileIdOrderByCreatedAtDesc(userId)
                    .stream()
                    .filter(a -> !a.getId().equals(addressId))
                    .collect(Collectors.toList());

            if (!otherAddresses.isEmpty()) {
                otherAddresses.get(0).setDefault(true);
                addressRepository.save(otherAddresses.get(0));
            }
        }

        addressRepository.delete(address);
    }

    /**
     * Set an address as default (automatically removes default flag from others).
     *
     * @param userId the user ID
     * @param addressId the address ID to set as default
     * @return the updated address response
     */
    @Override
    @Transactional
    public UserAddressResponse setDefaultAddress(UUID userId, UUID addressId) {
        findUserProfile(userId); // Verify user exists
        UserAddress address = addressRepository.findByIdAndUserProfileId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ADDRESS_NOT_FOUND,
                        "Address not found: " + addressId));

        // Remove default flag from all other addresses
        removeDefaultFlagFromOtherAddresses(userId);

        // Set this address as default
        address.setDefault(true);
        UserAddress savedAddress = addressRepository.save(address);
        return addressMapper.mapToResponse(savedAddress);
    }

    /**
     * Remove default flag from all addresses for a user.
     *
     * @param userId the user ID
     */
    @Transactional
    private void removeDefaultFlagFromOtherAddresses(UUID userId) {
        List<UserAddress> allAddresses = addressRepository.findByUserProfileIdOrderByCreatedAtDesc(userId);
        for (UserAddress address : allAddresses) {
            if (address.isDefault()) {
                address.setDefault(false);
            }
        }
        if (!allAddresses.isEmpty()) {
            addressRepository.saveAll(allAddresses);
        }
    }

    /**
     * Find user profile by ID (internal helper).
     *
     * @param userId the user ID
     * @return the user profile
     * @throws ResourceNotFoundException if user not found
     */
    private UserProfile findUserProfile(UUID userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND,
                        "User not found: " + userId));
    }
}
