package com.fixit.feature.worker.stats.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.stats.domain.model.WorkerStats;
import com.fixit.feature.worker.stats.domain.repository.WorkerStatsRepository;

import javax.inject.Inject;

public class GetWorkerStatsUseCase {

    private final WorkerStatsRepository workerStatsRepository;

    @Inject
    public GetWorkerStatsUseCase(WorkerStatsRepository workerStatsRepository) {
        this.workerStatsRepository = workerStatsRepository;
    }

    public void execute(String period, ResultCallback<WorkerStats> callback) {
        workerStatsRepository.getStats(period, callback);
    }
}
