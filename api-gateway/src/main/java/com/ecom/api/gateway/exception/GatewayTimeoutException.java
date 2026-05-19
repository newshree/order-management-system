package com.ecom.api.gateway.exception;

import com.ecom.api.gateway.enums.ErrorCode;

/**
 * Exception thrown when a gateway request times out.
 */
public class GatewayTimeoutException extends GatewayException {

	/**
	 * Constructs a timed out gateway exception.
	 *
	 * @param message human readable exception message
	 */
	public GatewayTimeoutException(String message) {
		super(message, ErrorCode.GATEWAY_TIMEOUT);
	}
}
