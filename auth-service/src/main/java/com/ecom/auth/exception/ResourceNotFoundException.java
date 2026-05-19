package com.ecom.auth.exception;

import com.ecom.auth.enums.ErrorCode;

/**
 * Exception thrown when a requested resource is not found.
 *
 * Used for HTTP 404 responses indicating that the requested entity does not exist in the system.
 */
public class ResourceNotFoundException extends RuntimeException {

	private final ErrorCode errorCode;

	/**
	 * Constructs a ResourceNotFoundException with an error code and message.
	 *
	 * @param errorCode the application error code for categorizing the error
	 * @param message the detailed error message describing what was not found
	 */
	public ResourceNotFoundException(ErrorCode errorCode, String message) {
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
