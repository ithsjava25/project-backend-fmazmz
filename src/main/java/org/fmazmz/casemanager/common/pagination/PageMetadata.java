package org.fmazmz.casemanager.common.pagination;

import org.springframework.data.domain.Page;

/**
 * Pagination metadata for API clients (page/offset based navigation).
 */
public record PageMetadata(
        int page,
        int size,
        long offset,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious
) {
    public static PageMetadata from(Page<?> page) {
        return new PageMetadata(
                page.getNumber(),
                page.getSize(),
                page.getPageable().getOffset(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}
