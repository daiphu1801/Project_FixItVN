package com.fixit.domain.upload.entity;

import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;

public enum UploadPurpose {
    AVATAR,
    WORKER_KYC_FRONT,
    WORKER_KYC_BACK,
    WORKER_KYC_SELFIE,
    WORKER_CERTIFICATE,
    BOOKING_ISSUE_IMAGE,
    PROOF_BEFORE_REPAIR,
    PROOF_AFTER_REPAIR,
    CHAT_IMAGE,
    COMPLAINT_EVIDENCE;

    public static UploadPurpose from(String value) {
        if (value == null || value.isBlank()) {
            throw new AppException(ErrorCode.INVALID_REQUEST_PARAMETER);
        }

        try {
            return UploadPurpose.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new AppException(ErrorCode.INVALID_REQUEST_PARAMETER);
        }
    }
}