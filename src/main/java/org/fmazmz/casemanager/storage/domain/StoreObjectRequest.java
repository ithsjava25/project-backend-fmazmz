package org.fmazmz.casemanager.storage.domain;

import java.io.InputStream;

public record StoreObjectRequest(
        String key,
        InputStream content,
        long contentLength,
        String contentType
) {
}
