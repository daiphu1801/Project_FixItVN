package com.fixit.feature.upload.data.mapper;

import com.fixit.feature.upload.data.remote.dto.response.PresignedUrlResponse;
import com.fixit.feature.upload.data.remote.dto.response.UploadedFileResponse;
import com.fixit.feature.upload.domain.model.ConfirmedUpload;
import com.fixit.feature.upload.domain.model.UploadTicket;

public final class UploadMapper {

    private UploadMapper() {}

    public static UploadTicket toTicket(PresignedUrlResponse response) {
        if (response == null) {
            return null;
        }

        return new UploadTicket(
                response.getUploadId(),
                response.getUploadUrl(),
                response.getMethod(),
                response.getObjectKey(),
                response.getFileUrl(),
                response.getStorageProvider(),
                expiresAtMillis(response),
                response.getFormData()
        );
    }

    public static ConfirmedUpload toConfirmedUpload(UploadedFileResponse response) {
        if (response == null) {
            return null;
        }

        return new ConfirmedUpload(
                response.getUploadId(),
                response.getPurpose(),
                response.getObjectKey(),
                response.getFileUrl(),
                response.getContentType(),
                response.getFileSize(),
                response.getStatus()
        );
    }

    private static long expiresAtMillis(PresignedUrlResponse response) {
        int expiresInSeconds = response.getExpiresInSeconds() != null
                ? response.getExpiresInSeconds()
                : 300;
        return System.currentTimeMillis() + Math.max(1, expiresInSeconds) * 1000L;
    }
}
