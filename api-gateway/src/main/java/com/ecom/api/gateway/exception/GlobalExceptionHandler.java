package com.ecom.api.gateway.exception;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ecom.api.gateway.dto.response.ErrorResponse;
import com.ecom.api.gateway.enums.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Global exception handler for the API Gateway.
 *
 * <p>Converts exceptions into structured {@link ErrorResponse} payloads.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * Handles custom {@link GatewayException} instances.
	 *
	 * @param ex gateway exception
	 * @param request current HTTP request
	 * @return error response entity with mapped HTTP status
	 */
	@ExceptionHandler(GatewayException.class)
	public ResponseEntity<ErrorResponse> handleGatewayException(
			GatewayException ex,
			HttpServletRequest request) {

		log.warn("Gateway exception. Path: {}, Message: {}", request.getRequestURI(), ex.getMessage());

		ErrorCode errorCode = ex.getErrorCode();
		HttpStatus status = mapErrorCodeToHttpStatus(errorCode);

		ErrorResponse error = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(status.value())
				.error(status.getReasonPhrase())
				.code(errorCode)
				.message(ex.getMessage())
				.path(request.getRequestURI())
				.build();

		return ResponseEntity.status(status).body(error);
	}

	/**
	 * Handles any uncaught exceptions and returns a generic internal server error response.
	 *
	 * @param ex unexpected exception
	 * @param request current HTTP request
	 * @return generic error response entity
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneralException(
			Exception ex,
			HttpServletRequest request) {

		log.error("Unhandled exception occurred for path: {}", request.getRequestURI(), ex);

		ErrorResponse error = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.error("INTERNAL_SERVER_ERROR")
				.code(ErrorCode.INTERNAL_SERVER_ERROR)
				.message("An unexpected error occurred. Please try again later.")
				.path(request.getRequestURI())
				.build();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

	/**
	 * Maps a gateway error code to an HTTP status.
	 *
	 * @param errorCode gateway error code
	 * @return corresponding {@link HttpStatus}
	 */
	private HttpStatus mapErrorCodeToHttpStatus(ErrorCode errorCode) {
		return switch (errorCode) {
		case INVALID_REQUEST -> HttpStatus.BAD_REQUEST;
		case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
		case FORBIDDEN -> HttpStatus.FORBIDDEN;
		case GATEWAY_TIMEOUT -> HttpStatus.GATEWAY_TIMEOUT;
		case SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
		case BAD_GATEWAY -> HttpStatus.BAD_GATEWAY;
		case RATE_LIMIT_EXCEEDED -> HttpStatus.TOO_MANY_REQUESTS;
		default -> HttpStatus.INTERNAL_SERVER_ERROR;
		};
	}
}
