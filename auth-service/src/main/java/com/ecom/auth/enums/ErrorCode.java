package com.ecom.auth.enums;

/**
 * Enumeration of application-specific error codes.
 *
 * Provides standardized error codes for various authentication and authorization scenarios.
 * Used to categorize errors for API responses and exception handling.
 */
public enum ErrorCode {

	    /** User account with the provided email already exists */
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS"),

    /** User account with the provided email was not found */
    USER_NOT_FOUND("USER_NOT_FOUND"),

    /** Provided credentials (email/password) are invalid */
    INVALID_CREDENTIALS("INVALID_CREDENTIALS"),

    /** Provided email is invalid */
    INVALID_EMAIL("INVALID_EMAIL"),

    /** Provided password is invalid */
    INVALID_PASSWORD("INVALID_PASSWORD"),

    /** JWT token is invalid or malformed */
    TOKEN_INVALID("TOKEN_INVALID"),

    /** JWT token has expired */
    TOKEN_EXPIRED("TOKEN_EXPIRED"),

    /** JWT signature validation failed */
    INVALID_TOKEN_SIGNATURE("INVALID_TOKEN_SIGNATURE"),

    /** Refresh token is invalid */
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN"),

    /** Refresh token has expired */
    REFRESH_TOKEN_EXPIRED("REFRESH_TOKEN_EXPIRED"),

    /** Authorization header missing */
    AUTH_HEADER_MISSING("AUTH_HEADER_MISSING"),

    /** Authorization header format invalid */
    AUTH_HEADER_INVALID("AUTH_HEADER_INVALID"),

    /** Request validation failed */
    VALIDATION_FAILED("VALIDATION_FAILED"),

    /** Request format or structure is invalid */
    INVALID_REQUEST("INVALID_REQUEST"),

    /** Request parameter value is invalid */
    INVALID_PARAMETER("INVALID_PARAMETER"),

    /** User is not authorized to perform the action */
    UNAUTHORIZED("UNAUTHORIZED"),

    /** User does not have permission to access the resource */
    FORBIDDEN("FORBIDDEN"),

    /** An unexpected server-side error occurred */
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),

    /** Database operation failed */
    DATABASE_ERROR("DATABASE_ERROR");

	private final String code;

	/**
	 * Constructs an ErrorCode enum with the given code string.
	 *
	 * @param code the string representation of the error code
	 */
	ErrorCode(String code) {
		this.code = code;
	}

	/**
	 * Returns the string representation of this error code.
	 *
	 * @return the error code as a string
	 */
	public String getCode() {
		return code;
	}
}
