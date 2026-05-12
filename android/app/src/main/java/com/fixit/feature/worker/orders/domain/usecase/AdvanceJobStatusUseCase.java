package com.fixit.feature.worker.orders.domain.usecase;

import com.fixit.feature.worker.orders.domain.model.JobStatus;
import com.fixit.feature.worker.orders.domain.repository.WorkerOrdersRepository;

import javax.inject.Inject;

public class AdvanceJobStatusUseCase {
    private final WorkerOrdersRepository repository;

    @Inject
    public AdvanceJobStatusUseCase(WorkerOrdersRepository repository) {
        this.repository = repository;
    }

    public JobStatus execute(JobStatus currentStatus) {
        return repository.advanceStatus(currentStatus);
    }
}
