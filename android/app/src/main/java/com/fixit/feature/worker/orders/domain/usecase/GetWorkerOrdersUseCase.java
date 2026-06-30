package com.fixit.feature.worker.orders.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.orders.domain.model.WorkerOrder;
import com.fixit.feature.worker.orders.domain.repository.WorkerOrdersRepository;

import java.util.List;

import javax.inject.Inject;

public class GetWorkerOrdersUseCase {
    private final WorkerOrdersRepository repository;

    @Inject
    public GetWorkerOrdersUseCase(WorkerOrdersRepository repository) {
        this.repository = repository;
    }

    public void execute(ResultCallback<List<WorkerOrder>> callback) {
        repository.getOrders(callback);
    }
}
