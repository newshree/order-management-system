package com.ecom.user.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.user.dto.request.UserAddressCreateRequest;
import com.ecom.user.dto.request.UserAddressUpdateRequest;
import com.ecom.user.dto.response.ApiResponse;
import com.ecom.user.dto.response.UserAddressResponse;
import com.ecom.user.service.UserAddressService;
import com.ecom.user.util.UserContext;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for User Address operations.
 *
 * Provides endpoints for managing user addresses (self-service).
 */
@RestController
@RequestMapping("/api/users/me/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final UserAddressService addressService;
    private final UserContext userContext;

    /**
     * Get all addresses for the current user.
     *
     * @return API response with list of addresses
     */
    @GetMapping("/getUserAddresses")
    public ResponseEntity<ApiResponse<List<UserAddressResponse>>> getUserAddresses() {
        UUID userId = userContext.getUserIdFromHeader();
        List<UserAddressResponse> addresses = addressService.getUserAddresses(userId);
        return ResponseEntity.ok(ApiResponse.<List<UserAddressResponse>>builder()
                .success(true)
                .data(addresses)
                .message("User addresses retrieved successfully")
                .build());
    }

    /**
     * Get default address for the current user.
     *
     * @return API response with default address
     */
    @GetMapping("/getDefaultAddress")
    public ResponseEntity<ApiResponse<UserAddressResponse>> getDefaultAddress() {
        UUID userId = userContext.getUserIdFromHeader();
        UserAddressResponse address = addressService.getDefaultAddress(userId);
        return ResponseEntity.ok(ApiResponse.<UserAddressResponse>builder()
                .success(true)
                .data(address)
                .message("Default address retrieved successfully")
                .build());
    }

    /**
     * Add a new address for the current user.
     *
     * @param request the create request
     * @return API response with created address
     */
    @PostMapping("/addAddress")
    public ResponseEntity<ApiResponse<UserAddressResponse>> addAddress(
            @Valid @RequestBody UserAddressCreateRequest request) {
        UUID userId = userContext.getUserIdFromHeader();
        UserAddressResponse address = addressService.addAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<UserAddressResponse>builder()
                .success(true)
                .data(address)
                .message("Address added successfully")
                .build());
    }

    /**
     * Get a specific address by ID for the current user.
     *
     * @param addressId the address ID
     * @return API response with address
     */
    @GetMapping("/getAddressById/{addressId}")
    public ResponseEntity<ApiResponse<UserAddressResponse>> getAddressById(
            @PathVariable UUID addressId) {
        UUID userId = userContext.getUserIdFromHeader();
        UserAddressResponse address = addressService.getAddressById(userId, addressId);
        return ResponseEntity.ok(ApiResponse.<UserAddressResponse>builder()
                .success(true)
                .data(address)
                .message("Address retrieved successfully")
                .build());
    }

    /**
     * Update an existing address.
     *
     * @param addressId the address ID
     * @param request the update request
     * @return API response with updated address
     */
    @PutMapping("/updateAddress/{addressId}")
    public ResponseEntity<ApiResponse<UserAddressResponse>> updateAddress(
            @PathVariable UUID addressId,
            @Valid @RequestBody UserAddressUpdateRequest request) {
        UUID userId = userContext.getUserIdFromHeader();
        UserAddressResponse address = addressService.updateAddress(userId, addressId, request);
        return ResponseEntity.ok(ApiResponse.<UserAddressResponse>builder()
                .success(true)
                .data(address)
                .message("Address updated successfully")
                .build());
    }

    /**
     * Delete an address.
     *
     * @param addressId the address ID
     * @return API response
     */
    @DeleteMapping("/deleteAddress/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable UUID addressId) {
        UUID userId = userContext.getUserIdFromHeader();
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Address deleted successfully")
                .build());
    }

    /**
     * Set an address as the default address.
     *
     * @param addressId the address ID to set as default
     * @return API response with updated address
     */
    @PutMapping("/setDefaultAddress/{addressId}")
    public ResponseEntity<ApiResponse<UserAddressResponse>> setDefaultAddress(
            @PathVariable UUID addressId) {
        UUID userId = userContext.getUserIdFromHeader();
        UserAddressResponse address = addressService.setDefaultAddress(userId, addressId);
        return ResponseEntity.ok(ApiResponse.<UserAddressResponse>builder()
                .success(true)
                .data(address)
                .message("Default address updated successfully")
                .build());
    }
}
