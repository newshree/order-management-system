package com.ecom.user.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.user.dto.response.ApiResponse;
import com.ecom.user.dto.response.UserAddressResponse;
import com.ecom.user.dto.response.UserProfileResponse;
import com.ecom.user.service.UserAddressService;
import com.ecom.user.service.UserProfileService;

import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Internal User Service APIs.
 *
 * These endpoints are intended for internal microservice-to-microservice communication.
 * They do NOT require X-User-Id header as they are called by other services.
 */
@RestController
@RequestMapping("/api/internal/users")
@RequiredArgsConstructor
public class InternalController {

    private final UserProfileService userProfileService;
    private final UserAddressService addressService;

    /**
     * Get user profile by ID (for internal service-to-service calls).
     *
     * @param userId the user ID
     * @return API response with user profile
     */
    @GetMapping("/getUserById/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(
            @PathVariable String userId) {
        UUID userUUID = UUID.fromString(userId);
        UserProfileResponse user = userProfileService.getUserById(userUUID);
        return ResponseEntity.ok(ApiResponse.<UserProfileResponse>builder()
                .success(true)
                .data(user)
                .message("User retrieved successfully")
                .build());
    }

    /**
     * Get default address for a user (for internal service-to-service calls).
     *
     * Useful for Order Service to get default shipping address during checkout.
     *
     * @param userId the user ID
     * @return API response with default address
     */
    @GetMapping("/getDefaultAddress/{userId}")
    public ResponseEntity<ApiResponse<UserAddressResponse>> getDefaultAddress(
            @PathVariable String userId) {
        UUID userUUID = UUID.fromString(userId);
        UserAddressResponse address = addressService.getDefaultAddress(userUUID);
        return ResponseEntity.ok(ApiResponse.<UserAddressResponse>builder()
                .success(true)
                .data(address)
                .message("Default address retrieved successfully")
                .build());
    }
}
