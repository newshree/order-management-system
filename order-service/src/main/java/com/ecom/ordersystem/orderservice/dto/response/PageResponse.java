package com.ecom.ordersystem.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Pagination Response conversion DTO
 *
 * @param <T>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {

    private List<T> content;

    private PageableInfo pageable;

    private long totalElements;

    private int totalPages;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PageableInfo {
        private int pageNumber;
        private int pageSize;
    }
}