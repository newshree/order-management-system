package com.ecom.api.gateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard wrapper used for successful API responses.
 *
 * @param <T> type of the response payload
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

	/** Indicates whether the operation was successful. */
	private boolean success;

	/** The returned data payload. */
	private T data;

	/** Human readable response message. */
	private String message;
}
