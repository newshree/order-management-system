package com.ecom.auth.util;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

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

	@Value("${jwt.expiration}")
	private long expiration;

	@Value("${jwt.refresh.expiration}")
	private long refreshExpiration;

	/**
	 * Generates a JWT access token for the specified email.
	 *
	 * @param email the subject (user email) to embed in the token
	 * @return a signed JWT token with the access token expiration time
	 */
	public String generateToken(String email) {
		return Jwts.builder()
				.setSubject(email)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	/**
	 * Generates a JWT refresh token for the specified email.
	 *
	 * @param email the subject (user email) to embed in the token
	 * @return a signed JWT token with the refresh token expiration time
	 */
	public String generateRefreshToken(String email) {
		return Jwts.builder()
				.setSubject(email)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	/**
	 * Extracts the email address from a JWT token.
	 *
	 * @param token the JWT token to parse
	 * @return the email address from the token's subject claim
	 */
	public String extractEmail(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
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
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Checks if a JWT token has expired.
	 *
	 * @param token the JWT token to check
	 * @return true if the token expiration time is before the current time, false otherwise
	 */
	public boolean isTokenExpired(String token) {
		try {
			Date expiration = Jwts.parserBuilder()
					.setSigningKey(getSigningKey())
					.build()
					.parseClaimsJws(token)
					.getBody()
					.getExpiration();
			return expiration.before(new Date());
		} catch (Exception ex) {
			return true;
		}
	}

	/**
	 * Retrieves the expiration duration in milliseconds for access tokens.
	 *
	 * @return the access token expiration duration in milliseconds
	 */
	public Long getExpirationMillis() {
		return expiration;
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
