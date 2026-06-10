package com.fixit.feature.worker.orders.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.orders.domain.model.JobStatus;
import com.fixit.feature.worker.orders.domain.repository.WorkerOrdersRepository;

import javax.inject.Inject;

public class AdvanceJobStatusUseCase {
    private final WorkerOrdersRepository repository;

    @Inject
    public AdvanceJobStatusUseCase(WorkerOrdersRepository repository) {
        this.repository = repository;
    }

    public void execute(String orderId, JobStatus currentStatus, ResultCallback<JobStatus> callback) {
        repository.advanceStatus(orderId, currentStatus, callback);
    }
}
