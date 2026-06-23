package com.fixit.feature.worker.job.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.job.domain.model.WorkerJobSummary;
import com.fixit.feature.worker.job.domain.repository.WorkerJobRepository;

import javax.inject.Inject;

public class GetWorkerJobSummaryUseCase {
    private final WorkerJobRepository repository;

    @Inject
    public GetWorkerJobSummaryUseCase(WorkerJobRepository repository) {
        this.repository = repository;
    }

    public void execute(ResultCallback<WorkerJobSummary> callback) {
        repository.getJobSummary(callback);
    }
}
