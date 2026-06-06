package com.fixit.feature.upload.domain.model;

import java.util.Map;

//Model sau khi gọi /uploads/presigned-url
public class UploadTicket {
    private final String uploadId;
    private final String uploadUrl;
    private final String method;
    private final String objectKey;
    private final String fileUrl;
    private final String storageProvider;
    private final long expiresAtMillis;
    private final Map<String, String> formData;

    public UploadTicket(
            String uploadId,
            String uploadUrl,
            String method,
            String objectKey,
            String fileUrl,
            String storageProvider,
            long expiresAtMillis,
            Map<String, String> formData
    ) {
        this.uploadId = uploadId;
        this.uploadUrl = uploadUrl;
        this.method = method;
        this.objectKey = objectKey;
        this.fileUrl = fileUrl;
        this.storageProvider = storageProvider;
        this.expiresAtMillis = expiresAtMillis;
        this.formData = formData;
    }

    public String getUploadId() {
        return uploadId;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public String getMethod() {
        return method;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getStorageProvider() {
        return storageProvider;
    }

    public long getExpiresAtMillis() {
        return expiresAtMillis;
    }

    public Map<String, String> getFormData() {
        return formData;
    }
}
