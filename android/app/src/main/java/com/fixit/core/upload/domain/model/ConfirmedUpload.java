package com.fixit.core.upload.domain.model;

public class ConfirmedUpload {
    private final String uploadId;
    private final String fileUrl;
    private final String storageKey;
    private final String contentType;
    private final long fileSizeBytes;
    private final UploadPurpose uploadPurpose;
    private final String referenceId;
    private final String confirmedAt;

    public ConfirmedUpload(
            String uploadId,
            String fileUrl,
            String storageKey,
            String contentType,
            long fileSizeBytes,
            UploadPurpose uploadPurpose,
            String referenceId,
            String confirmedAt
    ) {
        this.uploadId = uploadId;
        this.fileUrl = fileUrl;
        this.storageKey = storageKey;
        this.contentType = contentType;
        this.fileSizeBytes = fileSizeBytes;
        this.uploadPurpose = uploadPurpose;
        this.referenceId = referenceId;
        this.confirmedAt = confirmedAt;
    }

    public String getUploadId() {
        return uploadId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public String getContentType() {
        return contentType;
    }

    public long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public UploadPurpose getUploadPurpose() {
        return uploadPurpose;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public String getConfirmedAt() {
        return confirmedAt;
    }
}
