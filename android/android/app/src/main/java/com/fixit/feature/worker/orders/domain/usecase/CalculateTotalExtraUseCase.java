package com.fixit.feature.worker.orders.domain.usecase;

import com.fixit.feature.worker.orders.domain.repository.WorkerOrdersRepository;

import javax.inject.Inject;

public class CalculateTotalExtraUseCase {
    private final WorkerOrdersRepository repository;

    @Inject
    public CalculateTotalExtraUseCase(WorkerOrdersRepository repository) {
        this.repository = repository;
    }

    public long execute() {
        return repository.calculateTotalExtra();
    }
}
