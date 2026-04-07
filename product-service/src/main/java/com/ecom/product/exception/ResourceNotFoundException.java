package com.ecom.product.exception;

import com.ecom.product.enums.ErrorCode;

/**
 * Exception thrown when a requested resource cannot be found
 * in the system.
 *
 * This is commonly used when entities such as Products,
 * Categories, or Brands are requested but do not exist
 * in the database.
 *
 * Example usage:
 *
 * throw new ResourceNotFoundException(
 *     ErrorCode.PRODUCT_NOT_FOUND,
 *     "Product not found with id: " + productId
 * );
 */
public class ResourceNotFoundException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * Constructs a new ResourceNotFoundException with
     * the specified error code and detail message.
     *
     * @param errorCode specific error code representing the type of error
     * @param message detailed description of the error
     */
    public ResourceNotFoundException(ErrorCode errorCode, String message) {
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