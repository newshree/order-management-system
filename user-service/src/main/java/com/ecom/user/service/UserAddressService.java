package com.ecom.user.service;

import java.util.List;
import java.util.UUID;

import com.ecom.user.dto.request.UserAddressCreateRequest;
import com.ecom.user.dto.request.UserAddressUpdateRequest;
import com.ecom.user.dto.response.UserAddressResponse;

/**
 * Service interface for UserAddress operations.
 *
 * Defines business logic for managing user addresses.
 */
public interface UserAddressService {

    /**
     * Add a new address for a user.
     *
     * @param userId the user ID
     * @param request the create request
     * @return the created address response
     */
    UserAddressResponse addAddress(UUID userId, UserAddressCreateRequest request);

    /**
     * Get all addresses for a user.
     *
     * @param userId the user ID
     * @return list of address responses
     */
    List<UserAddressResponse> getUserAddresses(UUID userId);

    /**
     * Get default address for a user.
     *
     * @param userId the user ID
     * @return the default address response
     */
    UserAddressResponse getDefaultAddress(UUID userId);

    /**
     * Get a specific address by ID.
     *
     * @param userId the user ID
     * @param addressId the address ID
     * @return the address response
     */
    UserAddressResponse getAddressById(UUID userId, UUID addressId);

    /**
     * Update an address.
     *
     * @param userId the user ID
     * @param addressId the address ID
     * @param request the update request
     * @return the updated address response
     */
    UserAddressResponse updateAddress(UUID userId, UUID addressId, UserAddressUpdateRequest request);

    /**
     * Delete an address.
     *
     * @param userId the user ID
     * @param addressId the address ID
     */
    void deleteAddress(UUID userId, UUID addressId);

    /**
     * Set an address as default (automatically removes default flag from others).
     *
     * @param userId the user ID
     * @param addressId the address ID to set as default
     * @return the updated address response
     */
    UserAddressResponse setDefaultAddress(UUID userId, UUID addressId);
}
