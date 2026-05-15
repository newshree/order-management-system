package com.ecom.cart.exception;

import com.ecom.cart.enums.ErrorCode;

/**
 * Exception thrown when the client sends an invalid
 * or unacceptable request.
 *
 * This typically represents business validation errors
 * such as invalid user data or invalid input parameters,
 * or authorization/authentication errors.
 *
 * Example usage:
 *
 * throw new BadRequestException(
 *     ErrorCode.USER_ALREADY_EXISTS,
 *     "User with email already exists"
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