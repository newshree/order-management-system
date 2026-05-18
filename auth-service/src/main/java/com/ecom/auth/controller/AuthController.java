package com.ecom.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.auth.dto.request.LoginRequest;
import com.ecom.auth.dto.request.RegisterRequest;
import com.ecom.auth.dto.response.ApiResponse;
import com.ecom.auth.dto.response.AuthResponse;
import com.ecom.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST API Controller for authentication operations.
 *
 * Provides endpoints for user registration, login, token refresh, and token validation.
 * Handles Cross-Origin Resource Sharing (CORS) for the frontend client.
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and authorization endpoints")
public class AuthController {

	@Autowired
	private AuthService authService;

	/**
	 * Registers a new user in the system.
	 *
	 * @param request the registration request containing email and password
	 * @return a response entity with the registration success message
	 */
	@PostMapping("/register")
	@Operation(summary = "Register a new user", description = "Creates a new user account with email and password")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
	})
	public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
		authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ApiResponse.<String>builder()
						.success(true)
						.message("User registered successfully")
						.data("User account created. You can now login.")
						.build()
		);
	}

	/**
	 * Authenticates a user and generates JWT tokens.
	 *
	 * @param request the login request containing email and password
	 * @return a response entity with access token, refresh token, and user details
	 */
	@PostMapping("/login")
	@Operation(summary = "Authenticate user", description = "Authenticates user credentials and returns JWT tokens")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid credentials")
	})
	public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
		AuthResponse response = authService.login(request);
		return ResponseEntity.ok(
				ApiResponse.<AuthResponse>builder()
						.success(true)
						.message("Login successful")
						.data(response)
						.build()
		);
	}

	/**
	 * Refreshes an expired access token using a refresh token.
	 *
	 * @param authHeader the Authorization header containing the refresh token in Bearer format
	 * @return a response entity with a new access token and refresh token
	 */
	@PostMapping("/refresh")
	@Operation(summary = "Refresh access token", description = "Generates a new access token using a valid refresh token")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or missing refresh token")
	})
	public ResponseEntity<ApiResponse<AuthResponse>> refresh(
			@RequestHeader(value = "Authorization", required = false) String authHeader) {

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.badRequest().body(
					ApiResponse.<AuthResponse>builder()
							.success(false)
							.message("Refresh token is required in Authorization header")
							.build()
			);
		}

		String refreshToken = authHeader.substring(7);
		AuthResponse response = authService.refreshToken(refreshToken);

		return ResponseEntity.ok(
				ApiResponse.<AuthResponse>builder()
						.success(true)
						.message("Token refreshed successfully")
						.data(response)
						.build()
		);
	}

	/**
	 * Validates the authenticity and validity of a JWT token.
	 *
	 * @param authHeader the Authorization header containing the token in Bearer format
	 * @return a response entity with the validation result (true if valid, false otherwise)
	 */
	@GetMapping("/validate")
	@Operation(summary = "Validate JWT token", description = "Validates the authenticity and expiration of a JWT token")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token validation result returned"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Missing or invalid Authorization header")
	})
	public ResponseEntity<ApiResponse<Boolean>> validateToken(
			@RequestHeader(value = "Authorization", required = false) String authHeader) {

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.badRequest().body(
					ApiResponse.<Boolean>builder()
							.success(false)
							.message("Token is required in Authorization header")
							.data(false)
							.build()
			);
		}

		String token = authHeader.substring(7);
		boolean isValid = authService.validateToken(token);

		return ResponseEntity.ok(
				ApiResponse.<Boolean>builder()
						.success(isValid)
						.message(isValid ? "Token is valid" : "Token is invalid")
						.data(isValid)
						.build()
		);
	}
}
