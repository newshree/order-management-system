package com.ecom.auth.mapper;

import com.ecom.auth.dto.response.AuthResponse;
import com.ecom.auth.entity.User;

/**
 * Mapper interface for converting User entities to authentication response DTOs.
 *
 * Defines the contract for mapping user data along with JWT tokens into a unified response object.
 */
public interface AuthMapper {

	/**
	 * Converts a User entity and JWT tokens to an AuthResponse DTO.
	 *
	 * @param user the User entity to map
	 * @param accessToken the JWT access token
	 * @param refreshToken the JWT refresh token
	 * @return an AuthResponse containing user details and tokens
	 */
	AuthResponse toAuthResponse(User user, String accessToken, String refreshToken);
}
