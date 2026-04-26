package org.fmazmz.casemanager.storage.noop;

import org.fmazmz.casemanager.storage.domain.PresignedGetObjectResult;
import org.fmazmz.casemanager.storage.domain.StorageObject;
import org.fmazmz.casemanager.storage.domain.StorageService;
import org.fmazmz.casemanager.storage.domain.StoreObjectRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Used when {@code app.storage.provider=none} (e.g. tests) so the context still wires {@link StorageService}.
 */
@Service
@ConditionalOnProperty(prefix = "app.storage", name = "provider", havingValue = "none")
public class NoopStorageService implements StorageService {

    @Override
    public StorageObject upload(StoreObjectRequest request) {
        throw new IllegalStateException("Object storage is disabled (app.storage.provider=none). Set app.storage.provider=s3 to use uploads.");
    }

    @Override
    public void delete(String key) {
        // no-op
    }

    @Override
    public String resolveUrl(String key) {
        return "https://unconfigured.example.invalid/" + key;
    }

    @Override
    public PresignedGetObjectResult presignGetObject(
            String key,
            Duration validity,
            String fileName,
            String contentType
    ) {
        throw new IllegalStateException("Object storage is disabled (app.storage.provider=none). Set app.storage.provider=s3 to use time-limited view links.");
    }
}
