package com.fixit.feature.worker.job.domain.repository;

import com.fixit.feature.worker.job.domain.model.WorkerJobSummary;

public interface WorkerJobRepository {
    WorkerJobSummary getJobSummary();
}
