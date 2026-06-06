package com.fixit.feature.upload.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.upload.domain.model.ConfirmedUpload;
import com.fixit.feature.upload.domain.model.LocalUploadFile;
import com.fixit.feature.upload.domain.repository.UploadRepository;

import javax.inject.Inject;

public class UploadAndConfirmUseCase {

    private final UploadRepository repository;

    @Inject
    public UploadAndConfirmUseCase(UploadRepository repository) {
        this.repository = repository;
    }

    public void execute(
            LocalUploadFile file,
            ResultCallback<ConfirmedUpload> callback
    ) {
        repository.uploadAndConfirm(file, callback);
    }
}