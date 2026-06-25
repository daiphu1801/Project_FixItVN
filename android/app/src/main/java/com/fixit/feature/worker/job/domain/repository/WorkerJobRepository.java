package com.fixit.feature.worker.job.domain.repository;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.job.domain.model.WorkerJobSummary;

public interface WorkerJobRepository {
    void getJobSummary(ResultCallback<WorkerJobSummary> callback);
}
