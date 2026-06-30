package com.fixit.domain.worker.repository.projection;

public interface WorkerJobCompletionRateProjection {
    Integer getTotalJobs();
    Integer getCompletedJobs();
    Integer getCancelledJobs();
}
