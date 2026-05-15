package com.ecom.inventory.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for bulk stock update (admin operation).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkStockUpdateRequest {

    /**
     * List of stock updates to apply.
     */
    @NotEmpty(message = "Stock updates list cannot be empty")
    @Valid
    private List<StockUpdateRequest> stockUpdates;
}

