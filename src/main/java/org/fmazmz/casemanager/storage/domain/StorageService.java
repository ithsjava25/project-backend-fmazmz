package org.fmazmz.casemanager.storage.domain;

import java.time.Duration;

public interface StorageService {
    StorageObject upload(StoreObjectRequest request);

    void delete(String key);

    String resolveUrl(String key);

    /**
     * Produces a time-limited read URL for a stored object. For S3 this is a pre-signed GET
     * (functionally similar to a read-only SAS URL on Azure). {@code fileName} / {@code contentType} may
     * be null; when set, S3 will send matching response headers when the link is used.
     */
    PresignedGetObjectResult presignGetObject(String key, Duration validity, String fileName, String contentType);
}
