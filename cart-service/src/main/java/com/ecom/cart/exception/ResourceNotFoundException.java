package com.ecom.cart.exception;

import com.ecom.cart.enums.ErrorCode;

/**
 * Exception thrown when a requested resource cannot be found
 * in the system.
 *
 * This is commonly used when entities such as Users,
 * Addresses, or Preferences are requested but do not exist
 * in the database.
 *
 * Example usage:
 *
 * throw new ResourceNotFoundException(
 *     ErrorCode.USER_NOT_FOUND,
 *     "User not found with id: " + userId
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