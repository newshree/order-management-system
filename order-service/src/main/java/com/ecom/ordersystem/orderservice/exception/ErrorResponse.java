package com.ecom.ordersystem.orderservice.exception;

import com.ecom.ordersystem.orderservice.enums.ErrorCode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Standard API error response structure returned by the application
 * when an exception occurs.
 *
 * This class ensures consistent error responses across all APIs
 * in the microservice.
 *
 * Fields:
 * - timestamp : Time when the error occurred
 * - status    : HTTP status code
 * - error     : Short error type or category
 * - errorcode : Error codes used across the Order Service API.
 * - message   : Detailed error message
 * - path      : API endpoint where the error occurred
 *
 * Example response:
 *
 * {
 *   "timestamp": "2026-03-16T11:05:00",
 *   "status": 404,
 *   "error": "NOT_FOUND",
 *   "message": "Order not found with id: 123",
 *   "path": "/api/orders/123"
 * }
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
     * Error codes used across the Order Service API.
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
