package com.ecom.auth.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.ecom.auth.enums.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Global exception handler for the application.
 *
 * Centralized handler for catching and processing exceptions from the entire application.
 * Converts exceptions into standardized ErrorResponse DTOs with appropriate HTTP status codes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * Handles ResourceNotFoundException exceptions.
	 *
	 * @param ex the caught exception
	 * @param request the HTTP request
	 * @return a ResponseEntity with 404 status and error details
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(
			ResourceNotFoundException ex,
			HttpServletRequest request) {

		log.warn(
				"Resource not found. Path: {}, Message: {}",
				request.getRequestURI(),
				ex.getMessage()
		);

		ErrorResponse error = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.NOT_FOUND.value())
				.error("NOT_FOUND")
				.code(ex.getErrorCode())
				.message(ex.getMessage())
				.path(request.getRequestURI())
				.build();

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	/**
	 * Handles BadRequestException exceptions.
	 *
	 * Dynamically determines HTTP status based on the error code (400, 401, or 403).
	 *
	 * @param ex the caught exception
	 * @param request the HTTP request
	 * @return a ResponseEntity with appropriate status and error details
	 */
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResponse> handleBadRequest(
			BadRequestException ex,
			HttpServletRequest request) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		if (ex.getErrorCode() == ErrorCode.UNAUTHORIZED || ex.getErrorCode() == ErrorCode.FORBIDDEN) {
			status = ex.getErrorCode() == ErrorCode.UNAUTHORIZED ? HttpStatus.UNAUTHORIZED : HttpStatus.FORBIDDEN;
		}

		log.warn(
				"Client error occurred. Path: {}, Status: {}, Message: {}",
				request.getRequestURI(),
				status.value(),
				ex.getMessage()
		);

		ErrorResponse error = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(status.value())
				.error(status.getReasonPhrase())
				.code(ex.getErrorCode())
				.message(ex.getMessage())
				.path(request.getRequestURI())
				.build();

		return ResponseEntity.status(status).body(error);
	}

	/**
	 * Handles MethodArgumentNotValidException from request body validation.
	 *
	 * @param ex the caught exception
	 * @param request the HTTP request
	 * @return a ResponseEntity with 400 status and validation error details
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationError(
			MethodArgumentNotValidException ex,
			HttpServletRequest request) {

		String errors = ex.getBindingResult().getFieldErrors()
				.stream()
				.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
				.collect(Collectors.joining(", "));

		log.warn(
				"Validation failed. Path: {}, Errors: {}",
				request.getRequestURI(),
				errors
		);

		ErrorResponse error = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.BAD_REQUEST.value())
				.error("VALIDATION_ERROR")
				.code(ErrorCode.INVALID_REQUEST)
				.message("Validation failed: " + errors)
				.path(request.getRequestURI())
				.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	/**
	 * Handles MethodArgumentTypeMismatchException from invalid parameter types.
	 *
	 * @param ex the caught exception
	 * @param request the HTTP request
	 * @return a ResponseEntity with 400 status and type mismatch error details
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleTypeMismatch(
			MethodArgumentTypeMismatchException ex,
			HttpServletRequest request) {

		log.warn(
				"Type mismatch. Path: {}, Message: {}",
				request.getRequestURI(),
				ex.getMessage()
		);

		ErrorResponse error = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.BAD_REQUEST.value())
				.error("INVALID_PARAMETER")
				.code(ErrorCode.INVALID_PARAMETER)
				.message("Invalid value for parameter: " + ex.getName())
				.path(request.getRequestURI())
				.build();

		return ResponseEntity.badRequest().body(error);
	}

	/**
	 * Handles DataAccessException from database operations.
	 *
	 * @param ex the caught exception
	 * @param request the HTTP request
	 * @return a ResponseEntity with 500 status and database error details
	 */
	@ExceptionHandler(org.springframework.dao.DataAccessException.class)
	public ResponseEntity<ErrorResponse> handleDataAccessException(
			Exception ex,
			HttpServletRequest request) {

		log.warn(
				"Database access error. Path: {}, Message: {}",
				request.getRequestURI(),
				ex.getMessage()
		);

		ErrorResponse error = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.error("DATABASE_ERROR")
				.code(ErrorCode.DATABASE_ERROR)
				.message("Database operation failed")
				.path(request.getRequestURI())
				.build();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

	/**
	 * Fallback handler for all unhandled exceptions.
	 *
	 * @param ex the caught exception
	 * @param request the HTTP request
	 * @return a ResponseEntity with 500 status and generic error message
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
}
