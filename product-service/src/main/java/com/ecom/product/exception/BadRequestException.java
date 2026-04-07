package com.ecom.product.exception;

import com.ecom.product.enums.ErrorCode;

/**
 * Exception thrown when the client sends an invalid
 * or unacceptable request.
 *
 * This typically represents business validation errors
 * such as invalid product codes or invalid input parameters.
 *
 * Example usage:
 *
 * throw new BadRequestException(
 *     ErrorCode.DUPLICATE_PRODUCT,
 *     "Product code already exists"
 * );
 */
public class BadRequestException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * Constructs a new BadRequestException with
     * the specified error code and detail message.
     *
     * @param errorCode the error code
     * @param message detailed description of the error
     */
    public BadRequestException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Gets the error code for this exception.
     *
     * @return the error code
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}