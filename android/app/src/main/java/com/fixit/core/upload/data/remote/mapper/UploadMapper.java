package com.fixit.core.upload.data.remote.mapper;

import com.fixit.core.upload.data.remote.dto.UploadResponse;
import com.fixit.core.upload.domain.model.ConfirmedUpload;
import com.fixit.core.upload.domain.model.PresignedUpload;
import com.fixit.core.upload.domain.model.UploadPurpose;

public class UploadMapper {
    private UploadMapper() {
    }

    public static PresignedUpload toDomain(UploadResponse.PresignedUrl response) {
        return new PresignedUpload(
                response.getUploadId(),
                response.getUploadUrl(),
                response.getPublicUrl(),
                response.getStorageKey(),
                response.getExpiresInSeconds(),
                response.getHeaders()
        );
    }

    public static ConfirmedUpload toDomain(UploadResponse.ConfirmedUpload response) {
        UploadPurpose purpose = parsePurpose(response.getUploadPurpose());

        return new ConfirmedUpload(
                response.getUploadId(),
                response.getFileUrl(),
                response.getStorageKey(),
                response.getContentType(),
                response.getFileSizeBytes(),
                purpose,
                response.getReferenceId(),
                response.getConfirmedAt()
        );
    }

    private static UploadPurpose parsePurpose(String value) {
        if (value == null) {
            return null;
        }

        try {
            return UploadPurpose.valueOf(value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
