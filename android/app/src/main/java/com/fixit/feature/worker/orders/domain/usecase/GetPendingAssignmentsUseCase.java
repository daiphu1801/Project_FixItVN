package com.fixit.feature.worker.orders.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.orders.domain.model.WorkerAssignment;
import com.fixit.feature.worker.orders.domain.repository.WorkerOrdersRepository;

import java.util.List;
import javax.inject.Inject;

public class GetPendingAssignmentsUseCase {
    private final WorkerOrdersRepository repository;

    @Inject
    public GetPendingAssignmentsUseCase(WorkerOrdersRepository repository) {
        this.repository = repository;
    }

    public void execute(ResultCallback<List<WorkerAssignment>> callback) {
        repository.getPendingAssignments(callback);
    }
}
