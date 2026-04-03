package org.fmazmz.casemanager.common.pagination;

import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResult<T>(List<T> items, PageMetadata page) {
    public static <T> PagedResult<T> from(Page<T> page) {
        return new PagedResult<>(page.getContent(), PageMetadata.from(page));
    }
}
