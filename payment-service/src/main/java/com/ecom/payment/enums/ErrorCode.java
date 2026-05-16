package com.ecom.payment.enums;

/**
 * Enumeration of error codes used across the Payment Service API.
 *
 * This enum provides standardized error codes for consistent error
 * handling and client-side error identification.
 */
public enum ErrorCode {

    // Payment errors
    PAYMENT_NOT_FOUND("PAYMENT_NOT_FOUND"),
    PAYMENT_INVALID_AMOUNT("PAYMENT_INVALID_AMOUNT"),
    PAYMENT_PROCESSING_FAILED("PAYMENT_PROCESSING_FAILED"),
    PAYMENT_ALREADY_PROCESSED("PAYMENT_ALREADY_PROCESSED"),
    REFUND_FAILED("REFUND_FAILED"),
    REFUND_NOT_ELIGIBLE("REFUND_NOT_ELIGIBLE"),
    INVALID_PAYMENT_METHOD("INVALID_PAYMENT_METHOD"),
    ORDER_NOT_FOUND("ORDER_NOT_FOUND"),

    // General errors
    INVALID_REQUEST("INVALID_REQUEST"),
    INVALID_USER_ID("INVALID_USER_ID"),
    INVALID_PARAMETER("INVALID_PARAMETER"),
    UNAUTHORIZED("UNAUTHORIZED"),
    FORBIDDEN("FORBIDDEN"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),
    DATABASE_ERROR("DATABASE_ERROR");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
