package org.fmazmz.casemanager.storage.s3;

import lombok.extern.slf4j.Slf4j;
import org.fmazmz.casemanager.storage.domain.StorageService;
import org.fmazmz.casemanager.storage.domain.StoreObjectRequest;
import org.fmazmz.casemanager.storage.domain.StorageObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@ConditionalOnProperty(prefix = "app.storage", name = "provider", havingValue = "s3")
@Slf4j
public class S3StorageAdapter implements StorageService {
    private final S3Client s3Client;
    private final S3StorageProperties properties;

    public S3StorageAdapter(S3Client s3Client, S3StorageProperties s3Properties) {
        this.s3Client = s3Client;
        this.properties = s3Properties;
    }

    @Override
    public StorageObject upload(StoreObjectRequest request) {
        long startNs = System.nanoTime();
        String bucket = properties.getBucket();
        String region = properties.getRegion();
        String key = request.key();
        long contentLength = request.contentLength();
        String contentType = request.contentType();

        log.info(
                "S3 PutObject: invoking bucket={}, region={}, key={}, contentLength={}, contentType={}",
                bucket,
                region,
                key,
                contentLength,
                contentType
        );

        try {
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();
            RequestBody body = RequestBody.fromInputStream(request.content(), request.contentLength());
            PutObjectResponse putResponse = s3Client.putObject(req, body);
            long durationMs = (System.nanoTime() - startNs) / 1_000_000L;
            String eTag = putResponse != null && putResponse.eTag() != null ? putResponse.eTag() : "n/a";
            String versionId = putResponse != null && putResponse.versionId() != null ? putResponse.versionId() : "n/a";
            int sdkHttpStatus = putResponse != null && putResponse.sdkHttpResponse() != null
                    && putResponse.sdkHttpResponse().statusCode() > 0
                    ? putResponse.sdkHttpResponse().statusCode()
                    : HttpStatusCode.OK;

            String publicUrl = resolveUrl(key);
            log.info(
                    "S3 PutObject: success bucket={}, key={}, sdkHttpStatus={}, eTag={}, versionId={}, durationMs={}",
                    bucket,
                    key,
                    sdkHttpStatus,
                    eTag,
                    versionId,
                    durationMs
            );
            log.debug("S3 PutObject: resolved public URL for key={} (length={} chars)", key, publicUrl.length());

            return new StorageObject(key, publicUrl);
        } catch (S3Exception e) {
            long durationMs = (System.nanoTime() - startNs) / 1_000_000L;
            log.error(
                    "S3 PutObject: failed bucket={}, key={}, statusCode={}, awsErrorCode={}, requestId={}, extendedRequestId={}, durationMs={}",
                    bucket,
                    key,
                    e.statusCode(),
                    e.awsErrorDetails() != null ? e.awsErrorDetails().errorCode() : "n/a",
                    e.requestId() != null ? e.requestId() : "n/a",
                    e.extendedRequestId() != null ? e.extendedRequestId() : "n/a",
                    durationMs,
                    e
            );
            throw e;
        } catch (SdkException e) {
            long durationMs = (System.nanoTime() - startNs) / 1_000_000L;
            log.error(
                    "S3 PutObject: AWS SDK error bucket={}, key={}, durationMs={}",
                    bucket,
                    key,
                    durationMs,
                    e
            );
            throw e;
        }
    }

    @Override
    public void delete(String key) {
        long startNs = System.nanoTime();
        String bucket = properties.getBucket();
        log.info("S3 DeleteObject: invoking bucket={}, key={}", bucket, key);
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            DeleteObjectResponse response = s3Client.deleteObject(request);
            long durationMs = (System.nanoTime() - startNs) / 1_000_000L;
            int status = response != null && response.sdkHttpResponse() != null
                    ? response.sdkHttpResponse().statusCode()
                    : 0;
            log.info("S3 DeleteObject: success bucket={}, key={}, sdkHttpStatus={}, durationMs={}", bucket, key, status, durationMs);
        } catch (S3Exception e) {
            long durationMs = (System.nanoTime() - startNs) / 1_000_000L;
            log.error(
                    "S3 DeleteObject: failed bucket={}, key={}, statusCode={}, awsErrorCode={}, requestId={}, durationMs={}",
                    bucket,
                    key,
                    e.statusCode(),
                    e.awsErrorDetails() != null ? e.awsErrorDetails().errorCode() : "n/a",
                    e.requestId() != null ? e.requestId() : "n/a",
                    durationMs,
                    e
            );
            throw e;
        } catch (SdkException e) {
            long durationMs = (System.nanoTime() - startNs) / 1_000_000L;
            log.error("S3 DeleteObject: AWS SDK error bucket={}, key={}, durationMs={}", bucket, key, durationMs, e);
            throw e;
        }
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
