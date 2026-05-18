package com.ecom.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user login requests.
 *
 * Contains the email and password credentials for authenticating a user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

	/** User's email address, must be a valid email format */
	@NotBlank(message = "Email is required")
	@Email(message = "Email should be valid")
	private String email;

	/** User's password for authentication */
	@NotBlank(message = "Password is required")
	private String password;
}
