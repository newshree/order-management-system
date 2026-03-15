package com.ecom.ordersystem.orderservice.exception;

import com.ecom.ordersystem.orderservice.enums.ErrorCode;

/**
 * Exception thrown when a requested resource cannot be found
 * in the system.
 *
 * This is commonly used when entities such as Orders,
 * Users, or Products are requested but do not exist
 * in the database.
 *
 * Example usage:
 *
 * throw new ResourceNotFoundException(
 *     "Order not found with id: " + orderId
 * );
 */
public class ResourceNotFoundException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * Constructs a new ResourceNotFoundException with
     * the specified detail message and errorcode
     *
     * @param message detailed description of the error
     * @param errorCode specific error code representing the type of error
     */
    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}