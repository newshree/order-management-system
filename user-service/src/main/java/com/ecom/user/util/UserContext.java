package com.ecom.user.util;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ecom.user.enums.ErrorCode;
import com.ecom.user.exception.BadRequestException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility class for extracting user context from request headers.
 *
 * This class simulates authentication by extracting user information from
 * request headers. In a production system, this would be replaced by JWT
 * token parsing or OAuth2 token validation.
 *
 * Headers expected:
 * - X-User-Id: UUID of the authenticated user
 * - X-User-Role: Role of the user (USER, ADMIN)
 */
@Component
public class UserContext {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    /**
     * Extract the user ID from the request header.
     *
     * @return the user ID from X-User-Id header
     * @throws BadRequestException if header is missing or invalid
     */
    public UUID getUserIdFromHeader() {
        HttpServletRequest request = getHttpServletRequest();
        String userIdStr = request.getHeader(USER_ID_HEADER);

        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            throw new BadRequestException(ErrorCode.UNAUTHORIZED,
                    "Missing required header: " + USER_ID_HEADER);
        }

        try {
            return UUID.fromString(userIdStr);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ErrorCode.INVALID_USER_ID,
                    "Invalid user ID format in header: " + USER_ID_HEADER);
        }
    }

    /**
     * Extract the user role from the request header.
     *
     * @return the user role from X-User-Role header
     * @throws BadRequestException if header is missing
     */
    public String getUserRoleFromHeader() {
        HttpServletRequest request = getHttpServletRequest();
        String role = request.getHeader(USER_ROLE_HEADER);

        if (role == null || role.trim().isEmpty()) {
            throw new BadRequestException(ErrorCode.UNAUTHORIZED,
                    "Missing required header: " + USER_ROLE_HEADER);
        }

        return role.toUpperCase();
    }

    /**
     * Check if the user has admin role.
     *
     * @return true if user role is ADMIN, false otherwise
     */
    public boolean isAdmin() {
        try {
            String role = getUserRoleFromHeader();
            return "ADMIN".equals(role);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate that user has admin role.
     *
     * @throws BadRequestException if user is not an admin
     */
    public void validateAdminRole() {
        if (!isAdmin()) {
            throw new BadRequestException(ErrorCode.FORBIDDEN,
                    "Admin role required for this operation");
        }
    }

    /**
     * Get the current HttpServletRequest.
     *
     * @return the current HttpServletRequest
     * @throws BadRequestException if not in a request context
     */
    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST,
                    "Unable to extract request context");
        }
        return attributes.getRequest();
    }
}
