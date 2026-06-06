package com.fixit.feature.upload.domain.model;

//Model sau khi backend confirm xong.

public class ConfirmedUpload {
    private final String uploadId;
    private final String purpose;
    private final String objectKey;
    private final String fileUrl;
    private final String contentType;
    private final long fileSize;
    private final String status;

    public ConfirmedUpload(
            String uploadId,
            String purpose,
            String objectKey,
            String fileUrl,
            String contentType,
            long fileSize,
            String status
    ) {
        this.uploadId = uploadId;
        this.purpose = purpose;
        this.objectKey = objectKey;
        this.fileUrl = fileUrl;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.status = status;
    }

    public String getUploadId() {
        return uploadId;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getContentType() {
        return contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getStatus() {
        return status;
    }
}