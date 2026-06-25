package com.fixit.feature.upload.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

public class UploadedFileResponse {

    @SerializedName("uploadId")
    private String uploadId;

    @SerializedName("purpose")
    private String purpose;

    @SerializedName("objectKey")
    private String objectKey;

    @SerializedName("fileUrl")
    private String fileUrl;

    @SerializedName("contentType")
    private String contentType;

    @SerializedName("fileSize")
    private long fileSize;

    @SerializedName("status")
    private String status;

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