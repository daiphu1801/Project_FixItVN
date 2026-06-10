package com.fixit.feature.worker.orders.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.orders.domain.model.WorkerOrder;
import com.fixit.feature.worker.orders.domain.repository.WorkerOrdersRepository;

import javax.inject.Inject;

public class GetWorkerOrderByIdUseCase {
    private final WorkerOrdersRepository repository;

    @Inject
    public GetWorkerOrderByIdUseCase(WorkerOrdersRepository repository) {
        this.repository = repository;
    }

    public void execute(String orderId, ResultCallback<WorkerOrder> callback) {
        repository.getOrderById(orderId, callback);
    }
}
