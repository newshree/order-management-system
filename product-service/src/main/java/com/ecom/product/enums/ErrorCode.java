package com.ecom.product.enums;

/**
 * Enumeration of error codes used across the Product Service API.
 *
 * This enum provides standardized error codes for consistent error
 * handling and client-side error identification.
 */
public enum ErrorCode {
    BRAND_NOT_FOUND("BRAND_NOT_FOUND"),
    CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND"),
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND"),
    UNIT_OF_MEASURE_NOT_FOUND("UNIT_OF_MEASURE_NOT_FOUND"),
    INVALID_REQUEST("INVALID_REQUEST"),
    DUPLICATE_BRAND("DUPLICATE_BRAND"),
    DUPLICATE_CATEGORY("DUPLICATE_CATEGORY"),
    DUPLICATE_PRODUCT("DUPLICATE_PRODUCT"),
    DUPLICATE_UNIT_OF_MEASURE("DUPLICATE_UNIT_OF_MEASURE"),
    INVALID_CATEGORY_ID("INVALID_CATEGORY_ID"),
    INVALID_BRAND_ID("INVALID_BRAND_ID"),
    INVALID_UNIT_OF_MEASURE_ID("INVALID_UNIT_OF_MEASURE_ID"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
