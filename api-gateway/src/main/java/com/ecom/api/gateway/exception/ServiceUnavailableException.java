package com.ecom.api.gateway.exception;

import com.ecom.api.gateway.enums.ErrorCode;

/**
 * Exception thrown when a downstream service is unavailable.
 */
public class ServiceUnavailableException extends GatewayException {

	/**
	 * Constructs a service unavailable exception.
	 *
	 * @param message human readable exception message
	 */
	public ServiceUnavailableException(String message) {
		super(message, ErrorCode.SERVICE_UNAVAILABLE);
	}
}
