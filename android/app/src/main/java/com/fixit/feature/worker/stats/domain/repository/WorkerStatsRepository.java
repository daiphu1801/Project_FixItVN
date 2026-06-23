package com.fixit.feature.worker.stats.domain.repository;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.stats.domain.model.WorkerStats;

public interface WorkerStatsRepository {
    void getStats(String period, ResultCallback<WorkerStats> callback);
}
