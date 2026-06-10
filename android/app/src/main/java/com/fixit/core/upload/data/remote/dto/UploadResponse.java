package com.fixit.core.upload.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class UploadResponse {
    public static class PresignedUrl {
        @SerializedName("uploadId")
        private String uploadId;
        @SerializedName("uploadUrl")
        private String uploadUrl;
        @SerializedName("publicUrl")
        private String publicUrl;
        @SerializedName("storageKey")
        private String storageKey;
        @SerializedName("expiresInSeconds")
        private long expiresInSeconds;
        @SerializedName("headers")
        private Map<String, String> headers;

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

    public static class ConfirmedUpload {
        @SerializedName("uploadId")
        private String uploadId;
        @SerializedName("fileUrl")
        private String fileUrl;
        @SerializedName("publicUrl")
        private String publicUrl;
        @SerializedName("storageKey")
        private String storageKey;
        @SerializedName("contentType")
        private String contentType;
        @SerializedName("fileSizeBytes")
        private long fileSizeBytes;
        @SerializedName("uploadPurpose")
        private String uploadPurpose;
        @SerializedName("referenceId")
        private String referenceId;
        @SerializedName("confirmedAt")
        private String confirmedAt;

        public String getUploadId() {
            return uploadId;
        }

        public String getFileUrl() {
            return fileUrl != null ? fileUrl : publicUrl;
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

        public String getUploadPurpose() {
            return uploadPurpose;
        }

        public String getReferenceId() {
            return referenceId;
        }

        public String getConfirmedAt() {
            return confirmedAt;
        }
    }
}
