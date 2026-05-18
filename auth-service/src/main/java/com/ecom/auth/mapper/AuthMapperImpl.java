package com.ecom.auth.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecom.auth.dto.response.AuthResponse;
import com.ecom.auth.entity.User;
import com.ecom.auth.util.JwtUtil;

/**
 * Implementation of the AuthMapper interface.
 *
 * Converts User entities along with JWT tokens into AuthResponse DTOs
 * containing user details and token information.
 */
@Component
public class AuthMapperImpl implements AuthMapper {

	@Autowired
	private JwtUtil jwtUtil;

	/**
	 * Converts a User entity and JWT tokens to an AuthResponse DTO.
	 *
	 * @param user the User entity to map; if null, returns null
	 * @param accessToken the JWT access token for authentication
	 * @param refreshToken the JWT refresh token for token refresh operations
	 * @return an AuthResponse containing user ID, email, role, tokens, and expiration time
	 */
	@Override
	public AuthResponse toAuthResponse(User user, String accessToken, String refreshToken) {
		if (user == null) {
			return null;
		}

		return AuthResponse.builder()
				.userId(user.getId())
				.email(user.getEmail())
				.role(user.getRole())
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.expiresIn(jwtUtil.getExpirationMillis())
				.build();
	}
}
