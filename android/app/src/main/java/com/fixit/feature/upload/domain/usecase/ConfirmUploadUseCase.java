package com.fixit.feature.upload.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.upload.domain.model.ConfirmedUpload;
import com.fixit.feature.upload.domain.model.PendingUploadConfirm;
import com.fixit.feature.upload.domain.repository.UploadRepository;

import javax.inject.Inject;

public class ConfirmUploadUseCase {

    private final UploadRepository repository;

    @Inject
    public ConfirmUploadUseCase(UploadRepository repository) {
        this.repository = repository;
    }

    public void execute(
            PendingUploadConfirm pending,
            ResultCallback<ConfirmedUpload> callback
    ) {
        repository.confirmUpload(pending, callback);
    }
}