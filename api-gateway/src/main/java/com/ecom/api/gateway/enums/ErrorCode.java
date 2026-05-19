package com.ecom.api.gateway.enums;

/**
 * Defines application-specific error codes used by the gateway.
 */
public enum ErrorCode {
	INVALID_REQUEST("INVALID_REQUEST"),
	GATEWAY_TIMEOUT("GATEWAY_TIMEOUT"),
	SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE"),
	UNAUTHORIZED("UNAUTHORIZED"),
	FORBIDDEN("FORBIDDEN"),
	INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),
	BAD_GATEWAY("BAD_GATEWAY"),
	RATE_LIMIT_EXCEEDED("RATE_LIMIT_EXCEEDED");

	private final String code;

	ErrorCode(String code) {
		this.code = code;
	}

	/**
	 * Returns the string representation of the error code.
	 *
	 * @return error code string
	 */
	public String getCode() {
		return code;
	}
}
