package com.fixit.core.upload.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.core.upload.domain.model.PresignedUpload;
import com.fixit.core.upload.domain.model.UploadPurpose;
import com.fixit.core.upload.domain.repository.UploadRepository;

import javax.inject.Inject;

public class GetPresignedUploadUrlUseCase {
    private final UploadRepository repository;

    @Inject
    public GetPresignedUploadUrlUseCase(UploadRepository repository) {
        this.repository = repository;
    }

    public void execute(
            String fileName,
            String contentType,
            long fileSizeBytes,
            UploadPurpose uploadPurpose,
            String referenceId,
            ResultCallback<PresignedUpload> callback
    ) {
        repository.getPresignedUrl(fileName, contentType, fileSizeBytes, uploadPurpose, referenceId, callback);
    }
}
