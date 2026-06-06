package com.fixit.feature.worker.kyc.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.upload.data.remote.dto.request.WorkerKycSubmitRequest;
import com.fixit.feature.worker.kyc.domain.model.WorkerKyc;
import com.fixit.feature.worker.kyc.domain.repository.WorkerKycRepository;

import javax.inject.Inject;

public class SubmitKycUseCase {

    private final WorkerKycRepository workerKycRepository;

    @Inject
    public SubmitKycUseCase(WorkerKycRepository workerKycRepository) {
        this.workerKycRepository = workerKycRepository;
    }

    public void execute(WorkerKycSubmitRequest request, ResultCallback<WorkerKyc> callback) {
        workerKycRepository.submitKyc(request, callback);
    }
}
