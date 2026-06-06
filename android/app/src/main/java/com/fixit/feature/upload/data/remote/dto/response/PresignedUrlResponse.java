package com.fixit.feature.upload.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class PresignedUrlResponse {

    @SerializedName("uploadId")
    private String uploadId;

    @SerializedName("storageProvider")
    private String storageProvider;

    @SerializedName("uploadUrl")
    private String uploadUrl;

    @SerializedName("method")
    private String method;

    @SerializedName("objectKey")
    private String objectKey;

    @SerializedName("fileUrl")
    private String fileUrl;

    @SerializedName("expiresAt")
    private String expiresAt;

    @SerializedName("expiresInSeconds")
    private Integer expiresInSeconds;

    @SerializedName("formData")
    private Map<String, String> formData;

    public String getUploadId() {
        return uploadId;
    }

    public String getStorageProvider() {
        return storageProvider;
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

    public String getExpiresAt() {
        return expiresAt;
    }

    public Integer getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public Map<String, String> getFormData() {
        return formData;
    }
}
