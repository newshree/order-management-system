package com.ecom.cart.enums;

/**
 * Enumeration of error codes used across the Cart Service API.
 *
 * This enum provides standardized error codes for consistent error
 * handling and client-side error identification.
 */
public enum ErrorCode {
    // Cart errors
    CART_NOT_FOUND("CART_NOT_FOUND"),
    CART_ITEM_NOT_FOUND("CART_ITEM_NOT_FOUND"),
    CART_EMPTY("CART_EMPTY"),

    // Product errors
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND"),
    INVALID_PRODUCT_ID("INVALID_PRODUCT_ID"),
    INSUFFICIENT_INVENTORY("INSUFFICIENT_INVENTORY"),
    PRICE_MISMATCH("PRICE_MISMATCH"),

    // Validation errors
    INVALID_QUANTITY("INVALID_QUANTITY"),
    INVALID_PARAMETER("INVALID_PARAMETER"),
    VALIDATION_FAILED("VALIDATION_FAILED"),

    // General errors
    INVALID_REQUEST("INVALID_REQUEST"),
    INVALID_USER_ID("INVALID_USER_ID"),
    UNAUTHORIZED("UNAUTHORIZED"),
    FORBIDDEN("FORBIDDEN"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),
    DATABASE_ERROR("DATABASE_ERROR"),
    EXTERNAL_SERVICE_ERROR("EXTERNAL_SERVICE_ERROR");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
