package com.ecom.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper for all endpoints.
 * 
 * This class provides a standardized response format for all API endpoints,
 * including success/failure status, data payload, and informational messages.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    /**
     * Indicates if the request was successful.
     */
    private boolean success;

    /**
     * Response data (generic type T).
     */
    private T data;

    /**
     * Business validation messages or informational messages.
     */
    private String message;
}


