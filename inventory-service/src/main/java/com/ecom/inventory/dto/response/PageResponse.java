package com.ecom.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for paginated inventory results.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    private List<T> content;

    private PageableInfo pageable;

    private long totalElements;

    private int totalPages;

    private boolean first;

    private boolean last;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PageableInfo {

        private int pageNumber;

        private int pageSize;
    }
}

