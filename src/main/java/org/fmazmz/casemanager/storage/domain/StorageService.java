package org.fmazmz.casemanager.storage.domain;

public interface StorageService {
    StorageObject upload(StoreObjectRequest request);

    void delete(String key);

    String resolveUrl(String key);
}
