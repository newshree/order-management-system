package com.ecom.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration requests.
 *
 * Contains the email and password required for creating a new user account.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

	/** User's email address, must be unique and in valid email format */
	@NotBlank(message = "Email is required")
	@Email(message = "Email should be valid")
	private String email;

	/** User's password for the account, must be between 6 and 50 characters */
	@NotBlank(message = "Password is required")
	@Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters")
	private String password;
}
