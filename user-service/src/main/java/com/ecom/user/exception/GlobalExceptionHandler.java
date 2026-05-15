package com.ecom.user.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ecom.user.enums.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
/**
 * Global exception handler for the application.
 *
 * This class centralizes exception handling across all REST controllers
 * using Spring's @RestControllerAdvice.
 *
 * Benefits:
 * - Consistent error response structure
 * - Cleaner controllers and service classes
 * - Centralized error logging and management
 *
 * All exceptions thrown in controllers or services
 * will be intercepted here and converted into a
 * standardized API error response.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

        /**
         * Logger for exception handling.
         */
        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles ResourceNotFoundException.
     *
     * This exception is thrown when a requested resource
     * such as a User, Address, or Preferences cannot be found.
     *
     * @param ex the exception thrown
     * @param request HTTP request details
     * @return standardized API error response with HTTP 404
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
     * Handles BadRequestException.
     *
     * This exception represents client-side errors
     * such as invalid request parameters, unauthorized access,
     * or invalid business operations.
     *
     * @param ex the exception thrown
     * @param request HTTP request details
     * @return standardized API error response with HTTP 400
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        
        // Check if this is an authorization-related error
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
     * Handles validation errors triggered by
     * @Valid annotations on request DTOs.
     *
     * Examples:
     * - @NotNull
     * - @NotBlank
     * - @Email
     * - @Pattern
     *
     * This method collects all field validation errors
     * and returns them in a single response.
     *
     * @param ex validation exception
     * @param request HTTP request details
     * @return standardized API error response with HTTP 400
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
     * Handles all other exceptions not covered by specific handlers.
     *
     * @param ex the exception thrown
     * @param request HTTP request details
     * @return standardized API error response with HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex,
            HttpServletRequest request) {

        // FULL INTERNAL LOGGING
        log.error("Unhandled exception occurred for path: {}",
                request.getRequestURI(),
                ex);

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