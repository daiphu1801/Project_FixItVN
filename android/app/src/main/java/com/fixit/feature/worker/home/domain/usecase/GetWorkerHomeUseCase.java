package com.fixit.feature.worker.home.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.home.domain.model.WorkerHome;
import com.fixit.feature.worker.home.domain.repository.WorkerHomeRepository;

import javax.inject.Inject;

public class GetWorkerHomeUseCase {

    private final WorkerHomeRepository workerHomeRepository;

    @Inject
    public GetWorkerHomeUseCase(WorkerHomeRepository workerHomeRepository) {
        this.workerHomeRepository = workerHomeRepository;
    }

    public void execute(ResultCallback<WorkerHome> callback) {
        workerHomeRepository.getWorkerHome(callback);
    }
}