package org.fmazmz.casemanager.storage;

import java.io.InputStream;

public record StoreObjectRequest(
        String key,
        InputStream content,
        long contentLength,
        String contentType
) {
}
