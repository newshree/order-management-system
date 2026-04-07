package com.ecom.product.exception;

import com.ecom.product.enums.ErrorCode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Standard API error response structure returned by the application
 * when an exception occurs.
 *
 * This class ensures consistent error responses across all APIs
 * in the product microservice.
 *
 * Fields:
 * - timestamp : Time when the error occurred
 * - status    : HTTP status code
 * - error     : Short error type or category
 * - code      : Error codes used across the Product Service API
 * - message   : Detailed error message
 * - path      : API endpoint where the error occurred
 */
@Data
@Builder
public class ErrorResponse {

    /**
     * Time at which the error occurred.
     */
    private LocalDateTime timestamp;

    /**
     * HTTP status code associated with the error.
     */
    private int status;

    /**
     * Short error identifier (e.g., NOT_FOUND, BAD_REQUEST).
     */
    private String error;

    /**
     * Error codes used across the Product Service API.
     */
    private ErrorCode code;

    /**
     * Detailed message describing the error.
     */
    private String message;

    /**
     * API endpoint path where the error occurred.
     */
    private String path;
}
