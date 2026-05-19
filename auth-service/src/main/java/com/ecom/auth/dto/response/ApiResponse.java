package com.ecom.auth.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper for all REST endpoints.
 *
 * Provides a consistent response structure containing success status, message, data payload,
 * and timestamps. Null fields are excluded from JSON serialization.
 *
 * @param <T> the type of the data payload
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	/** Indicates whether the request was successful */
	private Boolean success;

	/** Human-readable message describing the response */
	private String message;

	/** The actual response data payload */
	private T data;

	/** Timestamp of when the event occurred */
	private LocalDateTime timestamp;

	/** Timestamp when the response was generated */
	@Builder.Default
	private LocalDateTime generatedAt = LocalDateTime.now();
}
