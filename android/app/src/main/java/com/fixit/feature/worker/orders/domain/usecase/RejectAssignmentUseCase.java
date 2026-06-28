package com.fixit.feature.worker.orders.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.orders.domain.repository.WorkerOrdersRepository;

import javax.inject.Inject;

public class RejectAssignmentUseCase {
    private final WorkerOrdersRepository repository;

    @Inject
    public RejectAssignmentUseCase(WorkerOrdersRepository repository) {
        this.repository = repository;
    }

    public void execute(String bookingId, String assignmentId, ResultCallback<String> callback) {
        repository.rejectAssignment(bookingId, assignmentId, callback);
    }
}
