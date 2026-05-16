package com.fixit.feature.worker.availability.domain.usecase;

import com.fixit.feature.worker.availability.domain.repository.WorkerAvailabilityRepository;

import javax.inject.Inject;

public class ToggleWorkerAvailabilityUseCase {
    private final WorkerAvailabilityRepository repository;

    @Inject
    public ToggleWorkerAvailabilityUseCase(WorkerAvailabilityRepository repository) {
        this.repository = repository;
    }

    public boolean execute() {
        return repository.toggleOnline();
    }
}
