package com.fixit.feature.worker.orders.domain.usecase;

import com.fixit.feature.worker.orders.domain.repository.WorkerOrdersRepository;

import javax.inject.Inject;

public class GenerateWorkerPaymentQrUseCase {
    private final WorkerOrdersRepository repository;

    @Inject
    public GenerateWorkerPaymentQrUseCase(WorkerOrdersRepository repository) {
        this.repository = repository;
    }

    public String execute(String orderId, long amount) {
        return repository.generatePaymentQrUrl(orderId, amount);
    }
}
