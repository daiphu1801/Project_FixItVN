package com.fixit.feature.upload.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.upload.domain.model.LocalUploadFile;
import com.fixit.feature.upload.domain.model.PendingUploadConfirm;
import com.fixit.feature.upload.domain.model.UploadTicket;
import com.fixit.feature.upload.domain.repository.UploadRepository;

import javax.inject.Inject;

public class UploadToCloudinaryUseCase {

    private final UploadRepository repository;

    @Inject
    public UploadToCloudinaryUseCase(UploadRepository repository) {
        this.repository = repository;
    }

    public void execute(
            UploadTicket ticket,
            LocalUploadFile file,
            ResultCallback<PendingUploadConfirm> callback
    ) {
        repository.uploadToCloudinary(ticket, file, callback);
    }
}