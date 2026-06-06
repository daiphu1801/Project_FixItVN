package com.fixit.feature.worker.kyc.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.kyc.domain.model.WorkerKyc;
import com.fixit.feature.worker.kyc.domain.repository.WorkerKycRepository;

import javax.inject.Inject;

public class GetKycStatusUseCase {

    private final WorkerKycRepository workerKycRepository;

    @Inject
    public GetKycStatusUseCase(WorkerKycRepository workerKycRepository) {
        this.workerKycRepository = workerKycRepository;
    }

    public void execute(ResultCallback<WorkerKyc> callback) {
        workerKycRepository.getKycStatus(callback);
    }
}
