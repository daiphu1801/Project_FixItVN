package com.fixit.feature.upload.presentation;

import com.fixit.feature.upload.domain.model.ConfirmedUpload;

/**
 * Kết quả upload đơn lẻ kèm purpose.
 * Giúp Fragment xác định ảnh nào upload xong để cập nhật đúng UI component.
 */
public class UploadResult {

    private final boolean success;
    private final String purpose;
    private final ConfirmedUpload confirmedUpload;
    private final String errorMessage;

    private UploadResult(boolean success, String purpose, ConfirmedUpload confirmedUpload, String errorMessage) {
        this.success = success;
        this.purpose = purpose;
        this.confirmedUpload = confirmedUpload;
        this.errorMessage = errorMessage;
    }

    public static UploadResult success(String purpose, ConfirmedUpload confirmedUpload) {
        return new UploadResult(true, purpose, confirmedUpload, null);
    }

    public static UploadResult error(String purpose, String errorMessage) {
        return new UploadResult(false, purpose, null, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getPurpose() {
        return purpose;
    }

    public ConfirmedUpload getConfirmedUpload() {
        return confirmedUpload;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
