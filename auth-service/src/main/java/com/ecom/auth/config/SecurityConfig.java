package com.ecom.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecom.auth.security.JwtAuthenticationFilter;

/**
 * Security configuration class for the application.
 *
 * Configures HTTP security, CSRF protection, session management, and JWT authentication filter.
 * Enables stateless authentication using JWT tokens.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	/**
	 * Configures the security filter chain for HTTP requests.
	 *
	 * - Disables CSRF protection (suitable for stateless APIs)
	 * - Configures stateless session management (no cookies)
	 * - Permits public access to authentication and Swagger/OpenAPI endpoints
	 * - Requires authentication for all other endpoints
	 * - Registers JWT filter before the standard authentication filter
	 *
	 * @param http the HttpSecurity object to configure
	 * @return a configured SecurityFilterChain
	 * @throws Exception if an error occurs during configuration
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
			.csrf(csrf -> csrf.disable())

			.sessionManagement(session -> session
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)

			.authorizeHttpRequests(auth -> auth
					.requestMatchers(
							"/api/auth/register",
							"/api/auth/login",
							"/api/auth/refresh",
							"/api/auth/validate",
							"/swagger-ui.html",
							"/swagger-ui/**",
							"/v3/api-docs/**",
							"/webjars/**"
					).permitAll()

					.anyRequest().authenticated()
			);

		http.addFilterBefore(
				jwtAuthenticationFilter,
				UsernamePasswordAuthenticationFilter.class
		);

		return http.build();
	}
}
