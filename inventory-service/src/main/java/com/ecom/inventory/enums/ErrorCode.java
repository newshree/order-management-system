package com.ecom.inventory.enums;

/**
 * Error codes used across all the API.
 *
 * These codes allow clients to programmatically identify
 * the type of error without parsing error messages.
 */
public enum ErrorCode {

    // Inventory-specific errors
    INVENTORY_NOT_FOUND("INVENTORY_NOT_FOUND"),
    INSUFFICIENT_STOCK("INSUFFICIENT_STOCK"),
    INVALID_RESERVATION_STATUS("INVALID_RESERVATION_STATUS"),
    DUPLICATE_RESERVATION("DUPLICATE_RESERVATION"),
    CONCURRENT_UPDATE_FAILED("CONCURRENT_UPDATE_FAILED"),
    RESERVATION_NOT_FOUND("RESERVATION_NOT_FOUND"),
    RESERVATION_ALREADY_COMMITTED("RESERVATION_ALREADY_COMMITTED"),

    // General errors
    INVALID_REQUEST("INVALID_REQUEST"),
    INVALID_USER_ID("INVALID_USER_ID"),
    UNAUTHORIZED("UNAUTHORIZED"),
    FORBIDDEN("FORBIDDEN"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),
    VALIDATION_FAILED("VALIDATION_FAILED"),
    DATABASE_ERROR("DATABASE_ERROR"),
    INVALID_PARAMETER("INVALID_PARAMETER");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
