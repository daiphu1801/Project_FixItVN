package com.fixit.core.upload.domain.model;

import java.util.Collections;
import java.util.Map;

public class PresignedUpload {
    private final String uploadId;
    private final String uploadUrl;
    private final String publicUrl;
    private final String storageKey;
    private final long expiresInSeconds;
    private final Map<String, String> headers;

    public PresignedUpload(
            String uploadId,
            String uploadUrl,
            String publicUrl,
            String storageKey,
            long expiresInSeconds,
            Map<String, String> headers
    ) {
        this.uploadId = uploadId;
        this.uploadUrl = uploadUrl;
        this.publicUrl = publicUrl;
        this.storageKey = storageKey;
        this.expiresInSeconds = expiresInSeconds;
        this.headers = headers == null ? Collections.emptyMap() : headers;
    }

    public String getUploadId() {
        return uploadId;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
