package com.ecom.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper for all endpoints.
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

