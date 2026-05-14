package com.ecom.cart.util;

import org.springframework.data.domain.Page;

import com.ecom.cart.dto.response.PageResponse;

/**
 * Utility class for converting Spring Data Page objects to custom PageResponse DTOs.
 * This class provides a method to build a PageResponse from a Page, extracting pagination metadata and content.
 * This helps standardize pagination responses across the application and decouples the API layer from Spring Data's Page implementation.
 * The buildPageResponse method takes a Page<T> and constructs a PageResponse<T> containing the content and pagination details.
 */
public class PaginationUtils {

    private PaginationUtils() {
    }

    public static <T> PageResponse<T> buildPageResponse(Page<T> page) {

        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageable(
                        PageResponse.PageableInfo.builder()
                                .pageNumber(page.getNumber())
                                .pageSize(page.getSize())
                                .build()
                )
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
