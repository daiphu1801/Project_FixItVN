package com.fixit.core.upload.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.core.upload.domain.model.ConfirmedUpload;
import com.fixit.core.upload.domain.model.UploadPurpose;
import com.fixit.core.upload.domain.repository.UploadRepository;

import javax.inject.Inject;

public class ConfirmUploadUseCase {
    private final UploadRepository repository;

    @Inject
    public ConfirmUploadUseCase(UploadRepository repository) {
        this.repository = repository;
    }

    public void execute(
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
    ) {
        repository.confirmUpload(
                uploadId,
                storageKey,
                publicUrl,
                fileName,
                contentType,
                fileSizeBytes,
                checksum,
                uploadPurpose,
                referenceId,
                callback
        );
    }
}
