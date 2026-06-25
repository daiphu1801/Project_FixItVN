package com.fixit.feature.upload.domain.model;

public final class PendingUploadStatus {
    private PendingUploadStatus() {
    }

    public static final String LOCAL_DRAFT = "LOCAL_DRAFT";
    public static final String LOCAL_SELECTED = "LOCAL_SELECTED";
    public static final String PRESIGNED_CREATED = "PRESIGNED_CREATED";
    public static final String CLOUDINARY_UPLOAD_FAILED = "CLOUDINARY_UPLOAD_FAILED";
    public static final String CLOUDINARY_UPLOADED = "CLOUDINARY_UPLOADED";
    public static final String CONFIRM_FAILED = "CONFIRM_FAILED";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String CONSUME_FAILED = "CONSUME_FAILED";
    public static final String CONSUMED = "CONSUMED";
}
