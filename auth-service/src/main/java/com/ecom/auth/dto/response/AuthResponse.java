package com.ecom.auth.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication response containing user details and JWT tokens.
 *
 * Returned after successful login or token refresh operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

	/** Unique identifier of the authenticated user */
	private UUID userId;

	/** Email address of the authenticated user */
	private String email;

	/** User's role in the system */
	private String role;

	/** JWT access token for API authentication */
	private String accessToken;

	/** JWT refresh token for generating new access tokens */
	private String refreshToken;

	/** Expiration time of the access token in milliseconds */
	private Long expiresIn;
}
