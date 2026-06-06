package com.fixit.feature.upload.domain.model;


//Model dùng để retry confirm nếu app mất mạng sau khi upload Cloudinary thành công.
public class PendingUploadConfirm {
    private final String uploadId;
    private final String objectKey;
    private final String fileUrl;
    private final String contentType;
    private final long fileSize;

    public PendingUploadConfirm(
            String uploadId,
            String objectKey,
            String fileUrl,
            String contentType,
            long fileSize
    ) {
        this.uploadId = uploadId;
        this.objectKey = objectKey;
        this.fileUrl = fileUrl;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }

    public String getUploadId() {
        return uploadId;
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
}