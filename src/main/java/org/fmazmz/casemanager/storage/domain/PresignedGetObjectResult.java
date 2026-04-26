package org.fmazmz.casemanager.storage.domain;

import java.time.Instant;

/**
 * A time-limited URL to read an object (S3: pre-signed GET; Azure equivalent: SAS read URL).
 */
public record PresignedGetObjectResult(String url, Instant expiresAt) {
}
