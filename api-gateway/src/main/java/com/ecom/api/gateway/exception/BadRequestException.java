package com.ecom.api.gateway.exception;

import com.ecom.api.gateway.enums.ErrorCode;

/**
 * Exception thrown when a client request is invalid or contains invalid data.
 *
 * Used for HTTP 400/401/403 responses indicating client-side errors that prevent processing.
 */
public class BadRequestException extends RuntimeException {

	private final ErrorCode errorCode;

	/**
	 * Constructs a BadRequestException with an error code and message.
	 *
	 * @param errorCode the application error code for categorizing the error
	 * @param message the detailed error message
	 */
	public BadRequestException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	/**
	 * Returns the error code associated with this exception.
	 *
	 * @return the ErrorCode enum value
	 */
	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
