package com.ecom.ordersystem.orderservice.enums;

/**
 * Error codes used across the Order Service API.
 *
 * These codes allow clients to programmatically identify
 * the type of error without parsing error messages.
 */
public enum ErrorCode {

    ORDER_NOT_FOUND,
    SHIPPING_ADDRESS_NOT_FOUND,
    INVALID_ORDER_STATE,
    VALIDATION_FAILED,
    DATABASE_ERROR,
    INTERNAL_SERVER_ERROR,
    INVALID_DATE_RANGE,
    INVALID_PARAMETER;
}
