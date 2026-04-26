package org.fmazmz.casemanager.ticket.dto;

import java.time.Instant;

/**
 * A time-limited URL to read an attachment in the browser (S3 pre-signed GET; same role as a read SAS in Azure).
 */
public record AttachmentViewUrlResponse(
        String url,
        Instant expiresAt
) {
}
