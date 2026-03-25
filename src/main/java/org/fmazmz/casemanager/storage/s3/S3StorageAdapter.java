package org.fmazmz.casemanager.storage.s3;

import org.fmazmz.casemanager.storage.StorageService;
import org.fmazmz.casemanager.storage.StoreObjectRequest;
import org.fmazmz.casemanager.storage.StorageObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@ConditionalOnProperty(prefix = "app.storage", name = "provider", havingValue = "s3")
public class S3StorageAdapter implements StorageService {
    private final S3Client s3Client;
    private final S3StorageProperties properties;

    public S3StorageAdapter(S3Client s3Client, S3StorageProperties s3Properties) {
        this.s3Client = s3Client;
        this.properties = s3Properties;
    }

    @Override
    public StorageObject upload(StoreObjectRequest request) {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(request.key())
                .contentType(request.contentType())
                .build();
        s3Client.putObject(req, RequestBody.fromInputStream(request.content(), request.contentLength()));
        return new StorageObject(request.key(), resolveUrl(request.key()));
    }

    @Override
    public void delete(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .build();
        s3Client.deleteObject(request);
    }

    @Override
    public String resolveUrl(String key) {
        if (properties.getPublicBaseUrl() != null && !properties.getPublicBaseUrl().isBlank()) {
            return properties.getPublicBaseUrl().replaceAll("/+$", "") + "/" + key;
        }
        return "https://%s.s3.%s.amazonaws.com/%s"
                .formatted(properties.getBucket(), properties.getRegion(), key);
    }
}
