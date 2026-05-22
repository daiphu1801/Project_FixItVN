package com.fixit.feature.worker.profile.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.profile.domain.model.WorkerProfile;
import com.fixit.feature.worker.profile.domain.repository.WorkerProfileRepository;

import javax.inject.Inject;

public class GetWorkerProfileUseCase {

    private final WorkerProfileRepository repository;

    @Inject
    public GetWorkerProfileUseCase(WorkerProfileRepository repository) {
        this.repository = repository;
    }

    public void execute(ResultCallback<WorkerProfile> callback) {
        repository.getProfile(callback);
    }
}