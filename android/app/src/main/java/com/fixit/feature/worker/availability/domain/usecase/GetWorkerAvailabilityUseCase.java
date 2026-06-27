package com.fixit.feature.worker.availability.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.availability.domain.repository.WorkerAvailabilityRepository;

import javax.inject.Inject;

public class GetWorkerAvailabilityUseCase {
    private final WorkerAvailabilityRepository repository;

    @Inject
    public GetWorkerAvailabilityUseCase(WorkerAvailabilityRepository repository) {
        this.repository = repository;
    }

    public void execute(ResultCallback<Boolean> callback) {
        repository.isOnline(callback);
    }
}
