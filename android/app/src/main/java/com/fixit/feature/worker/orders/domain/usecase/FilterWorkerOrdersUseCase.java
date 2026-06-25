package com.fixit.feature.worker.orders.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.orders.domain.model.WorkerOrder;
import com.fixit.feature.worker.orders.domain.repository.WorkerOrdersRepository;

import java.util.List;

import javax.inject.Inject;

public class FilterWorkerOrdersUseCase {
    private final WorkerOrdersRepository repository;

    @Inject
    public FilterWorkerOrdersUseCase(WorkerOrdersRepository repository) {
        this.repository = repository;
    }

    public void execute(String status, ResultCallback<List<WorkerOrder>> callback) {
        repository.filterOrders(status, callback);
    }
}
