package com.fixit.feature.upload.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

public class CloudinaryUploadResponse {

    @SerializedName("public_id")
    private String publicId;

    @SerializedName("secure_url")
    private String secureUrl;

    @SerializedName("resource_type")
    private String resourceType;

    @SerializedName("format")
    private String format;

    @SerializedName("bytes")
    private long bytes;

    public String getPublicId() {
        return publicId;
    }

    public String getSecureUrl() {
        return secureUrl;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getFormat() {
        return format;
    }

    public long getBytes() {
        return bytes;
    }
}