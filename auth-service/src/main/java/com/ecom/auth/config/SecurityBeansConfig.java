package com.ecom.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for security-related beans.
 *
 * Provides Spring beans for password encoding using BCrypt algorithm.
 */
@Configuration
public class SecurityBeansConfig {

	/**
	 * Creates a BCryptPasswordEncoder bean for password hashing and verification.
	 *
	 * @return a PasswordEncoder instance using BCrypt algorithm
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
