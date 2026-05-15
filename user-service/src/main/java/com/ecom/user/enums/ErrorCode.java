package com.ecom.user.enums;

/**
 * Enumeration of error codes used across the User Service API.
 *
 * This enum provides standardized error codes for consistent error
 * handling and client-side error identification.
 */
public enum ErrorCode {
    // User Profile errors
    USER_NOT_FOUND("USER_NOT_FOUND"),
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS"),
    USER_INACTIVE("USER_INACTIVE"),
    USER_DELETED("USER_DELETED"),
    
    // User Address errors
    ADDRESS_NOT_FOUND("ADDRESS_NOT_FOUND"),
    ADDRESS_ALREADY_DEFAULT("ADDRESS_ALREADY_DEFAULT"),
    NO_DEFAULT_ADDRESS("NO_DEFAULT_ADDRESS"),
    
    // User Preferences errors
    PREFERENCES_NOT_FOUND("PREFERENCES_NOT_FOUND"),
    
    // General errors
    INVALID_REQUEST("INVALID_REQUEST"),
    INVALID_USER_ID("INVALID_USER_ID"),
    UNAUTHORIZED("UNAUTHORIZED"),
    FORBIDDEN("FORBIDDEN"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
