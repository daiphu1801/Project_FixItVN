package com.fixit.feature.upload.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.upload.domain.model.LocalUploadFile;
import com.fixit.feature.upload.domain.model.UploadTicket;
import com.fixit.feature.upload.domain.repository.UploadRepository;

import javax.inject.Inject;

public class RequestUploadUrlUseCase {

    private final UploadRepository repository;

    @Inject
    public RequestUploadUrlUseCase(UploadRepository repository) {
        this.repository = repository;
    }

    public void execute(LocalUploadFile file, ResultCallback<UploadTicket> callback) {
        repository.requestPresignedUrl(file, callback);
    }
}