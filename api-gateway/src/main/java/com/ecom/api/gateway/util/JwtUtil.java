package com.ecom.api.gateway.util;

import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ecom.api.gateway.enums.ErrorCode;
import com.ecom.api.gateway.exception.BadRequestException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;

/**
 * Utility class for JWT (JSON Web Token) operations.
 *
 * Provides methods to generate, validate, and extract information from JWT tokens
 * using HMAC SHA-256 signing algorithm. Configurable through application properties.
 */
@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secret;

	/**
	 * Extracts the email address from JWT subject claim.
	 *
	 * @param token JWT token
	 * @return email address
	 */
	public String extractEmail(String token) {
		return extractClaims(token).getSubject();
	}

	/**
	 * Extracts the user ID from JWT custom claim.
	 *
	 * @param token JWT token
	 * @return user ID
	 */
	public String extractUserId(String token) {
		return extractClaims(token)
				.get("userId", String.class);
	}

	/**
	 * Extracts the user role from JWT custom claim.
	 *
	 * @param token JWT token
	 * @return role
	 */
	public String extractRole(String token) {
		return extractClaims(token)
				.get("role", String.class);
	}

	/**
	 * Extracts all claims from JWT token.
	 *
	 * @param token JWT token
	 * @return Claims object
	 */
	private Claims extractClaims(String token) {

		try {

			return Jwts.parserBuilder()
					.setSigningKey(getSigningKey())
					.build()
					.parseClaimsJws(token)
					.getBody();

		} catch (ExpiredJwtException ex) {

			throw new BadRequestException(
					ErrorCode.TOKEN_EXPIRED,
					"JWT token has expired"
			);

		} catch (MalformedJwtException ex) {

			throw new BadRequestException(
					ErrorCode.TOKEN_INVALID,
					"Malformed JWT token"
			);

		} catch (SecurityException ex) {

			throw new BadRequestException(
					ErrorCode.INVALID_TOKEN_SIGNATURE,
					"Invalid JWT signature"
			);

		} catch (JwtException ex) {

			throw new BadRequestException(
					ErrorCode.TOKEN_INVALID,
					"Invalid JWT token"
			);
		}
	}

	/**
	 * Validates the authenticity and integrity of a JWT token.
	 *
	 * @param token the JWT token to validate
	 * @return true if the token is valid, false if it's malformed, expired, or has invalid signature
	 */
	public boolean validateToken(String token) {
		try {

			Jwts.parserBuilder()
					.setSigningKey(getSigningKey())
					.build()
					.parseClaimsJws(token);

			return true;

		} catch (ExpiredJwtException ex) {

			throw new BadRequestException(
					ErrorCode.TOKEN_EXPIRED,
					"JWT token has expired"
			);

		} catch (MalformedJwtException ex) {

			throw new BadRequestException(
					ErrorCode.TOKEN_INVALID,
					"Malformed JWT token"
			);

		} catch (SecurityException ex) {

			throw new BadRequestException(
					ErrorCode.TOKEN_INVALID,
					"Invalid JWT signature"
			);

		} catch (JwtException ex) {

			throw new BadRequestException(
					ErrorCode.TOKEN_INVALID,
					"Invalid JWT token"
			);
		}
	}

	/**
	 * Retrieves the HMAC SHA-256 signing key derived from the configured secret.
	 *
	 * @return the Key object used for signing and verifying JWTs
	 */
	private Key getSigningKey() {
		byte[] keyBytes = secret.getBytes();
		return Keys.hmacShaKeyFor(keyBytes);
	}

	
}
