package com.ecom.ordersystem.orderservice.exception;

import com.ecom.ordersystem.orderservice.enums.ErrorCode;

/**
 * Exception thrown when the client sends an invalid
 * or unacceptable request.
 *
 * This typically represents business validation errors
 * such as invalid order status transitions or
 * invalid input parameters.
 *
 * Example usage:
 *
 * throw new BadRequestException(
 *     "Order cannot be cancelled after shipment"
 * );
 */
public class BadRequestException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * Constructs a new BadRequestException with
     * the specified detail message.
     *
     * @param message detailed description of the error
     */
    public BadRequestException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}