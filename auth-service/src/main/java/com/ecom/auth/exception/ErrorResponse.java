package com.ecom.auth.exception;

import java.time.LocalDateTime;

import com.ecom.auth.enums.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for error responses returned by the API.
 *
 * Contains error details including timestamp, HTTP status, error code, message, and request path.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

	/** Timestamp when the error occurred */
	private LocalDateTime timestamp;

	/** HTTP status code of the error response */
	private int status;

	/** HTTP status reason phrase (e.g., "Not Found", "Bad Request") */
	private String error;

	/** Application-specific error code for error categorization */
	private ErrorCode code;

	/** Human-readable error message */
	private String message;

	/** The request path that resulted in the error */
	private String path;
}
