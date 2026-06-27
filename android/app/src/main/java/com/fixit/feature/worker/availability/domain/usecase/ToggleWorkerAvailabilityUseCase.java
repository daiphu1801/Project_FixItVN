package com.fixit.feature.worker.availability.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.availability.domain.repository.WorkerAvailabilityRepository;

import javax.inject.Inject;

public class ToggleWorkerAvailabilityUseCase {
    private final WorkerAvailabilityRepository repository;

    @Inject
    public ToggleWorkerAvailabilityUseCase(WorkerAvailabilityRepository repository) {
        this.repository = repository;
    }

    public void execute(boolean online, ResultCallback<Boolean> callback) {
        repository.setOnline(online, callback);
    }
}
