package org.fmazmz.casemanager.utils;

import java.time.Instant;
import java.util.UUID;

public record ApiErrorResponse(
        String error,
        String message,
        int status,
        UUID requestId,
        Long timestamp
) {
    public ApiErrorResponse(String error, String message, int status) {
        this(error, message, status, UUID.randomUUID(), Instant.now().toEpochMilli());
    }
}