package com.fixit.feature.upload.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;

public class PresignedUrlRequest {

    @SerializedName("purpose")
    private final String purpose;

    @SerializedName("originalFileName")
    private final String originalFileName;

    @SerializedName("contentType")
    private final String contentType;

    @SerializedName("fileSize")
    private final long fileSize;

    public PresignedUrlRequest(
            String purpose,
            String originalFileName,
            String contentType,
            long fileSize
    ) {
        this.purpose = purpose;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }
}