package com.ecom.api.gateway.exception;

import com.ecom.api.gateway.enums.ErrorCode;

/**
 * Base runtime exception for API Gateway error handling.
 */
public class GatewayException extends RuntimeException {

	private final ErrorCode errorCode;

	/**
	 * Constructs a gateway exception with a message and error code.
	 *
	 * @param message human readable exception message
	 * @param errorCode associated {@link ErrorCode}
	 */
	public GatewayException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	/**
	 * Returns the associated gateway error code.
	 *
	 * @return error code for this exception
	 */
	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
