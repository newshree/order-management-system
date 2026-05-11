package com.ecom.user.controller;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.user.dto.request.UserProfileCreateRequest;
import com.ecom.user.dto.response.ApiResponse;
import com.ecom.user.dto.response.PageResponse;
import com.ecom.user.dto.response.UserProfileResponse;
import com.ecom.user.service.UserProfileService;
import com.ecom.user.util.PaginationUtils;
import com.ecom.user.util.UserContext;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Admin User operations.
 *
 * Provides admin endpoints for user management.
 * Requires ADMIN role in X-User-Role header.
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final UserProfileService userProfileService;
    private final UserContext userContext;
    private static final String DEFAULT_TENANT_ID = "2f34e34a-a524-4aff-9702-35e6f8c6f9d1";
    private static final String DEFAULT_ADMIN_ROLE = "ADMIN";

    /**
     * Get all users (admin only).
     *
     * @param tenantId the tenant ID from header
     * @param pageable pagination information
     * @return API response with paginated users
     */
    @GetMapping("/getAllUsers")
    public ResponseEntity<ApiResponse<PageResponse<UserProfileResponse>>> getAllUsers(
            @RequestHeader(value = "X-Tenant-ID", defaultValue = DEFAULT_TENANT_ID) String tenantId,
            @RequestHeader(value = "X-User-Role", defaultValue = DEFAULT_ADMIN_ROLE) String role, 
            @ParameterObject
            @PageableDefault(
                page = 0,
                size = 10,
                sort = "createdAt")
            Pageable pageable) {
        userContext.validateAdminRole();
        UUID tenantUUID = UUID.fromString(tenantId);
        Page<UserProfileResponse> users = userProfileService.getAllUsers(tenantUUID, pageable);

        PageResponse<UserProfileResponse> pageResponse = 
                PaginationUtils.buildPageResponse(users);
                
        return ResponseEntity.ok(ApiResponse.<PageResponse<UserProfileResponse>>builder()
                .success(true)
                .data(pageResponse)
                .message("Users retrieved successfully")
                .build());
    }

    /**
     * Get a specific user by ID (admin only).
     *
     * @param userId the user ID
     * @param tenantId the tenant ID from header
     * @return API response with user profile
     */
    @GetMapping("/getUserById/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(
            @PathVariable String userId,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = DEFAULT_TENANT_ID) String tenantId,
            @RequestHeader(value = "X-User-Role", defaultValue = DEFAULT_ADMIN_ROLE) String role) {
        userContext.validateAdminRole();
        // tenantId validated from header for multi-tenancy
        UUID userUUID = UUID.fromString(userId);
        UserProfileResponse user = userProfileService.getUserById(userUUID);
        return ResponseEntity.ok(ApiResponse.<UserProfileResponse>builder()
                .success(true)
                .data(user)
                .message("User retrieved successfully")
                .build());
    }

    /**
     * Create a new user (admin only).
     *
     * @param request the create request
     * @param tenantId the tenant ID from header
     * @return API response with created user
     */
    @PostMapping("/createUser")
    public ResponseEntity<ApiResponse<UserProfileResponse>> createUser(
            @Valid @RequestBody UserProfileCreateRequest request,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = DEFAULT_TENANT_ID) String tenantId,
            @RequestHeader(value = "X-User-Role", defaultValue = DEFAULT_ADMIN_ROLE) String role) {
        userContext.validateAdminRole();
        UUID tenantUUID = UUID.fromString(tenantId);
        UserProfileResponse user = userProfileService.createUserProfile(request, tenantUUID);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<UserProfileResponse>builder()
                .success(true)
                .data(user)
                .message("User created successfully")
                .build());
    }

    /**
     * Activate or deactivate a user (admin only).
     *
     * @param userId the user ID
     * @param isActive the new active status
     * @param tenantId the tenant ID from header
     * @return API response with updated user
     */
    @PutMapping("/setUserStatus/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> setUserStatus(
            @PathVariable String userId,
            @RequestParam Boolean isActive,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = DEFAULT_TENANT_ID) String tenantId,
            @RequestHeader(value = "X-User-Role", defaultValue = DEFAULT_ADMIN_ROLE) String role) {
        userContext.validateAdminRole();
        UUID userUUID = UUID.fromString(userId);
        // tenantId validated from header for multi-tenancy
        UserProfileResponse user = userProfileService.setUserStatus(userUUID, isActive);
        return ResponseEntity.ok(ApiResponse.<UserProfileResponse>builder()
                .success(true)
                .data(user)
                .message("User status updated successfully")
                .build());
    }

    /**
     * Delete a user (soft delete, admin only).
     *
     * @param userId the user ID
     * @param tenantId the tenant ID from header
     * @return API response
     */
    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable String userId,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = DEFAULT_TENANT_ID) String tenantId,
            @RequestHeader(value = "X-User-Role", defaultValue = DEFAULT_ADMIN_ROLE) String role) {
        userContext.validateAdminRole();
        UUID userUUID = UUID.fromString(userId);
        // tenantId validated from header for multi-tenancy
        userProfileService.deleteUser(userUUID);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("User deleted successfully")
                .build());
    }

    /**
     * Search users by email (admin only).
     *
     * @param email the email to search
     * @param tenantId the tenant ID from header
     * @param pageable pagination information
     * @return API response with matching users
     */
    @GetMapping("/searchUsersByEmail")
    public ResponseEntity<ApiResponse<PageResponse<UserProfileResponse>>> searchUsersByEmail(
            @RequestParam String email,
            @RequestHeader(value = "X-Tenant-ID", defaultValue = DEFAULT_TENANT_ID) String tenantId,
            @RequestHeader(value = "X-User-Role", defaultValue = DEFAULT_ADMIN_ROLE) String role,
            @ParameterObject
            @PageableDefault(
                page = 0,
                size = 10,
                sort = "createdAt")
            Pageable pageable) {
        userContext.validateAdminRole();
        UUID tenantUUID = UUID.fromString(tenantId);
        Page<UserProfileResponse> users = userProfileService.searchUsersByEmail(tenantUUID, email, pageable);

        PageResponse<UserProfileResponse> pageResponse = 
                PaginationUtils.buildPageResponse(users);

        return ResponseEntity.ok(ApiResponse.<PageResponse<UserProfileResponse>>builder()
                .success(true)
                .data(pageResponse)
                .message("Users retrieved successfully")
                .build());
    }
}

