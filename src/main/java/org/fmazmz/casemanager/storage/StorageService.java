package org.fmazmz.casemanager.storage;

public interface StorageService {
    StorageObject upload(StoreObjectRequest request);

    void delete(String key);

    String resolveUrl(String key);
}
