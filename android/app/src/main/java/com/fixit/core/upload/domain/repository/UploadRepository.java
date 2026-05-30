package com.fixit.core.upload.domain.repository;

import com.fixit.core.common.ResultCallback;
import com.fixit.core.upload.domain.model.ConfirmedUpload;
import com.fixit.core.upload.domain.model.PresignedUpload;
import com.fixit.core.upload.domain.model.UploadPurpose;

public interface UploadRepository {
    void getPresignedUrl(
            String fileName,
            String contentType,
            long fileSizeBytes,
            UploadPurpose uploadPurpose,
            String referenceId,
            ResultCallback<PresignedUpload> callback
    );

    void confirmUpload(
            String uploadId,
            String storageKey,
            String publicUrl,
            String fileName,
            String contentType,
            long fileSizeBytes,
            String checksum,
            UploadPurpose uploadPurpose,
            String referenceId,
            ResultCallback<ConfirmedUpload> callback
    );
}
