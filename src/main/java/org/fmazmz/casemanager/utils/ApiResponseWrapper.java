package org.fmazmz.casemanager.utils;

import java.time.Instant;
import java.util.UUID;

public record ApiResponseWrapper<T>(
        T data,
        UUID requestId,
        Long timestamp
)
{
    public ApiResponseWrapper(T data) {
        this(data, UUID.randomUUID(), Instant.now().toEpochMilli());
    }
}