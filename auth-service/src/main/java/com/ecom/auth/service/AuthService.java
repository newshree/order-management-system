package com.ecom.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecom.auth.dto.request.LoginRequest;
import com.ecom.auth.dto.request.RegisterRequest;
import com.ecom.auth.dto.response.AuthResponse;
import com.ecom.auth.entity.User;
import com.ecom.auth.enums.ErrorCode;
import com.ecom.auth.exception.BadRequestException;
import com.ecom.auth.exception.ResourceNotFoundException;
import com.ecom.auth.mapper.AuthMapper;
import com.ecom.auth.repository.UserRepository;
import com.ecom.auth.util.JwtUtil;

/**
 * Service class for authentication operations.
 *
 * Handles user registration, login, token refresh, token validation, and email extraction
 * from tokens. Manages user credentials and JWT token generation/validation.
 */
@Service
public class AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthMapper authMapper;

	/**
	 * Registers a new user in the system.
	 *
	 * @param request the registration request containing email and password
	 * @throws BadRequestException if a user with the provided email already exists
	 */
	public void register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BadRequestException(
					ErrorCode.USER_ALREADY_EXISTS,
					"User with email " + request.getEmail() + " already exists"
			);
		}

		User user = User.builder()
				.email(request.getEmail())
				.passwordHash(passwordEncoder.encode(request.getPassword()))
				.role("CUSTOMER")
				.isActive(true)
				.build();

		userRepository.save(user);
	}

	/**
	 * Authenticates a user and generates JWT tokens.
	 *
	 * @param request the login request containing email and password
	 * @return an AuthResponse containing access token, refresh token, and user details
	 * @throws ResourceNotFoundException if the user is not found
	 * @throws BadRequestException if the password is invalid or the user account is inactive
	 */
	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException(
						ErrorCode.USER_NOT_FOUND,
						"User with email " + request.getEmail() + " not found"
				));

		if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
			throw new BadRequestException(
					ErrorCode.INVALID_CREDENTIALS,
					"Invalid email or password"
			);
		}

		if (!user.getIsActive()) {
			throw new BadRequestException(
					ErrorCode.UNAUTHORIZED,
					"User account is inactive"
			);
		}

		String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getId(), user.getRole());
		String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getId(), user.getRole());

		return authMapper.toAuthResponse(user, accessToken, refreshToken);
	}

	/**
	 * Generates a new access token using a valid refresh token.
	 *
	 * @param refreshToken the refresh token from which to generate a new access token
	 * @return an AuthResponse containing the new access token and user details
	 * @throws BadRequestException if the refresh token is invalid
	 * @throws ResourceNotFoundException if the user associated with the token is not found
	 */
	public AuthResponse refreshToken(String refreshToken) {

		jwtUtil.validateToken(refreshToken);

		String email = jwtUtil.extractEmail(refreshToken);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException(
						ErrorCode.USER_NOT_FOUND,
						"User not found"
				));

		String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getId(), user.getRole());

		return authMapper.toAuthResponse(user, accessToken, refreshToken);
	}

	/**
	 * Validates the authenticity and expiration of a JWT token.
	 *
	 * @param token the JWT token to validate
	 * @return true if the token is valid and not expired, false otherwise
	 */
	public boolean validateToken(String token) {
		return jwtUtil.validateToken(token);
	}
}
