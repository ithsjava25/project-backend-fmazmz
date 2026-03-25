package org.fmazmz.casemanager.storage.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.storage.s3")
public class S3StorageProperties {
    private String bucket;
    private String region;
    private String publicBaseUrl;
}