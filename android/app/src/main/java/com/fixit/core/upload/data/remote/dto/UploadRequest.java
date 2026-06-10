package com.fixit.core.upload.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class UploadRequest {
    public static class PresignedUrl {
        @SerializedName("fileName")
        private final String fileName;
        @SerializedName("contentType")
        private final String contentType;
        @SerializedName("fileSizeBytes")
        private final long fileSizeBytes;
        @SerializedName("uploadPurpose")
        private final String uploadPurpose;
        @SerializedName("referenceId")
        private final String referenceId;

        public PresignedUrl(
                String fileName,
                String contentType,
                long fileSizeBytes,
                String uploadPurpose,
                String referenceId
        ) {
            this.fileName = fileName;
            this.contentType = contentType;
            this.fileSizeBytes = fileSizeBytes;
            this.uploadPurpose = uploadPurpose;
            this.referenceId = referenceId;
        }
    }

    public static class Confirm {
        @SerializedName("uploadId")
        private final String uploadId;
        @SerializedName("storageKey")
        private final String storageKey;
        @SerializedName("publicUrl")
        private final String publicUrl;
        @SerializedName("fileName")
        private final String fileName;
        @SerializedName("contentType")
        private final String contentType;
        @SerializedName("fileSizeBytes")
        private final long fileSizeBytes;
        @SerializedName("checksum")
        private final String checksum;
        @SerializedName("uploadPurpose")
        private final String uploadPurpose;
        @SerializedName("referenceId")
        private final String referenceId;

        public Confirm(
                String uploadId,
                String storageKey,
                String publicUrl,
                String fileName,
                String contentType,
                long fileSizeBytes,
                String checksum,
                String uploadPurpose,
                String referenceId
        ) {
            this.uploadId = uploadId;
            this.storageKey = storageKey;
            this.publicUrl = publicUrl;
            this.fileName = fileName;
            this.contentType = contentType;
            this.fileSizeBytes = fileSizeBytes;
            this.checksum = checksum;
            this.uploadPurpose = uploadPurpose;
            this.referenceId = referenceId;
        }
    }
}
