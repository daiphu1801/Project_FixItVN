package com.fixit.feature.worker.orders.domain.usecase;

import com.fixit.feature.worker.orders.domain.model.JobStatus;
import com.fixit.feature.worker.orders.domain.repository.WorkerOrdersRepository;

import javax.inject.Inject;

public class GetInitialJobStatusUseCase {
    private final WorkerOrdersRepository repository;

    @Inject
    public GetInitialJobStatusUseCase(WorkerOrdersRepository repository) {
        this.repository = repository;
    }

    public JobStatus execute(String orderStatus) {
        return repository.getInitialStatus(orderStatus);
    }
}
