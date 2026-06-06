package com.fixit.feature.upload.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;

public class UploadConfirmRequest {

    @SerializedName("uploadId")
    private final String uploadId;

    @SerializedName("objectKey")
    private final String objectKey;

    @SerializedName("fileUrl")
    private final String fileUrl;

    @SerializedName("contentType")
    private final String contentType;

    @SerializedName("fileSize")
    private final long fileSize;

    public UploadConfirmRequest(
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
}