package com.ecom.api.gateway.dto.response;

import java.time.LocalDateTime;

import com.ecom.api.gateway.enums.ErrorCode;

import lombok.Builder;
import lombok.Data;

/**
 * Structured response payload returned for error conditions.
 */
@Data
@Builder
public class ErrorResponse {

	/** Timestamp when the error occurred. */
	private LocalDateTime timestamp;

	/** HTTP status code associated with the error. */
	private int status;

	/** HTTP reason phrase for the status code. */
	private String error;

	/** Application-specific error code. */
	private ErrorCode code;

	/** Human readable error message. */
	private String message;

	/** Request path that caused the error. */
	private String path;
}
