package com.fixit.feature.worker.job.data.repository;

import com.fixit.feature.worker.job.domain.model.WorkerJobSummary;
import com.fixit.feature.worker.job.domain.repository.WorkerJobRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkerJobRepositoryImpl implements WorkerJobRepository {
    @Inject
    public WorkerJobRepositoryImpl() {
    }

    @Override
    public WorkerJobSummary getJobSummary() {
        return new WorkerJobSummary("Nguyen Van Phu", "Quan Cau Giay, Ha Noi", 5, 4.8f, 0.0);
    }
}
