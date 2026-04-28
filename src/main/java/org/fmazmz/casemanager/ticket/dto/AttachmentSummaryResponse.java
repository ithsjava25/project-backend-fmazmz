package org.fmazmz.casemanager.ticket.dto;

import java.time.Instant;
import java.util.UUID;

public record AttachmentSummaryResponse(
        UUID id,
        String fileName,
        String contentType,
        Long fileSize,
        String uploadedByEmail,
        Instant createdAt
) {
}
